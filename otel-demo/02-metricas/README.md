# Metricas Metricas Metricas

Bueno ahora vamos a tomar las metricas, en este caso estaremos trabajando con otel, pero vamos a enviar metricas a prometheus, y ver el resutlado de las mismas desde grafana.

Por lo tanto, lo primero que vamos a hacer es instalar prometheus y grafana.

## Instalacion de Prometheus

Para instalar seguimos esta guia que lo explica bien: https://medium.com/@gayatripawar401/deploy-prometheus-and-grafana-on-kubernetes-using-helm-5aa9d4fbae66

primero traer el chart:

```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update
```

Segundo crear el values y editar las partes que sean necesarias:

```bash
helm show values prometheus-community/prometheus > values.yaml
vim values.yaml
```

hacemos las modificaciones que necesitamos sobre el values.yaml para que no se genere el alermanager, bajar la retencion de datos, etc.

Ahora a generar el deploy.yaml para instarlo

```bash
helm template prometheus prometheus-community/prometheus --namespace prometheus --values values.yaml > deploy.yaml
```

Ahora instalar el deploy:

```bash
kubectl create ns prometheus
kubectl apply -f deploy.yaml
```

Nota: todos estos archivos se encuentran en la carpeta prometheus


## Instalacion de Grafana

Siguiendo el post anterior, pero ahora instalando grafana.

primero traer el chart:

```bash
helm repo add grafana https://grafana.github.io/helm-charts 
helm repo update
```

Segundo crear el values y editar las partes que sean necesarias:

```bash
helm show values grafana/grafana > values.yaml
vim values.yaml
```

hacemos las modificaciones que necesitamos sobre el values.yaml por ahora solo activamos el servicio para que sea NodePort y ponemos el valor al node port en 30300...

Ahora a generar el deploy.yaml para instarlo

```bash
helm template grafana grafana/grafana --namespace grafana --values values.yaml > deploy.yaml
```

Ahora instalar el deploy:

```bash
kubectl create ns grafana
kubectl apply -f deploy.yaml
```

El usuario para entrar en grafana es parte de los secret del namespace de grafana, puede revisar su valor en:

```bash
kubectl get secret -n grafana grafana -o yaml
```

Nota: todos estos archivos se encuentran en la carpeta grafana.


### Configuracion de Grafana y Prometheus

hay que configurar el acceso desde grafana a prometheus en la url: http://IP_DEL_SERVER:30300/connections/datasources/new

en la parte de "Connection" en donde pide la url de prometheus ponemos "http://prometheus-server.prometheus.svc.cluster.local"

Y estamos listos, "Save and test"


# Configurando Open telemetry con prometheus

## Primero el collector

Para esto vamos a tomar el collector anterior que tenia solo trazas, y vamos a sumar la configuracion de prometheus.

Para esto lo que tenemos que hacer es dentro de los exporters dar a conocer el endpoint de prometheus:

```yaml
      prometheus:
        endpoint: "0.0.0.0:8889"
        resource_to_telemetry_conversion:
          enabled: true
```

Y luego tenemos que sumar en la parte de services.pipelines la parte de metrics, de esta forma comenzamos a tomar metricas de prometheus de los servicios java y exponer a prometheus.

```yaml
        metrics:
          receivers: [otlp]
          exporters: [debug, prometheus]
```

La configuracion de prometheus esta dada de forma automatica por los annotation (SEGUIR ACA)


## Configuracion en la instrumentacion

En este caso lo que vamos a hacer es sumar solamente dos variables de entorno para explicitar que tipo de metric exporter ocupar "otlp" y como mandar los valores con el "cumulative".

```yaml
    - name: OTEL_METRICS_EXPORTER
      value: "otlp"
    - name: OTEL_EXPORTER_OTLP_METRICS_TEMPORALITY_PREFERENCE
      value: "cumulative"
```

Despues de este ultimo paso, es necesario reiniciar los pods de nuestro servicio de frases.

PERO, momento, voy a grafana y todavia no funciona????

Exacto, porque falta una patita mas, el collector tiene las metricas, pero prometheus no esta configurado para descubrir el pod y tomar las metricas, por lo tanto, ahora hay que configurar un job en prometheus...

## Configurando Job en prometheus

Al poner el puerto 8889, lo que hicimos fue que se abra un nuevo puerto en el pod (el 8889) en donde estan todas las metricas que llegan desde la autoinstrumentacion al collector, ahora hay que hacer que estas metricas se lean en prometheus.

Para eso vamos a crear un nuevo job en prometheus.

Vamos al configmap que existe en el namespace de prometheus y lo editamos.


```bash
kubectl get cm -n prometheus
```

El configmap se llama prometheus-server, asi que lo editamos

```bash
kubectl edit cm -n prometheus prometheus-server
```

Buscamos el prometheus.yml y en el mismo, en la parte del scrape_configs ponemos:

```yaml
  prometheus.yml: |
    # .... mucha config ....
    scrape_configs:
      # .... muchos otros jobs ....
      - job_name: 'otel-collector-app-metrics'
        scrape_interval: 2m
        kubernetes_sd_configs:
          - role: pod
        relabel_configs:
        - source_labels: [__meta_kubernetes_namespace]
          action: keep
          regex: collector
        - source_labels: [__meta_kubernetes_pod_label_app_kubernetes_io_name]
          action: keep
          regex: quotes-collector.*
        - source_labels: [__address__]
          action: replace
          regex: ([^:]+)(?::\d+)?
          replacement: $1:8889
          target_label: __address__
        - source_labels: [__meta_kubernetes_pod_name]
          action: replace
          target_label: kubernetes_pod_name
        metric_relabel_configs:
        - source_labels: [__name__]
          regex: (.*)
          target_label: __name__
          replacement: quote_services_${1}
```

Salvamos esto y cerramos, prometheus deberia comenzar a tener metricas genericas (dado a que estamos auto instrumentando) de los pods del "quote services".


## Que paso aqui?

Basicamente lo que hicimos aqui fue tomar lo que ya tenias en el paso anterior, pero ahora sumamos la parte de metricas.

Si bien las metricas son bastantes simples, la idea es que podamos agregar metricas mas especificas a nuestros artefactos (algo mas del "negocio"), la auto instrumentacion es simple y poderosa para comenzar, pero despues hay que ir agregandole los detalles necesarios.


