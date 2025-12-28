# Instalacion

Estos son los primeros pasos, aca vamos a instalar todo lo necesario para otel, y luego vamos a instalar Jaeger para ver trazas de nuesto artefacto de frases (el servicio del gateway-api y el quotes-services).

En siguientes carpetas vamos a estar revisando como trabajar con metricas, logs y como podemos configurar estos tres pilares para servicios de alguna nube.

Asi que lo que vamos a comenzar haciendo es instalando el operador de open telemetry que nos va a hacer la vida mucho mas simple para comenzar con las trazas, pero antes de ello, es importante instalar el cert manager (requisito del operador de otel).

Luego de esto, vamos a instalar Jaeger y al tenerlo listo configuraremos el collector y la instrumentacion para los artefactos.

La configuracion que hay que hacer a nuestro servicio de frases. y veremos las primeras trazas al llamar al servicio.

## instalar cert Manager

se debe instalar cert manager antes del otel-operator.

```bash
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.19.2/cert-manager.yaml
```

Documentacion:
- https://cert-manager.io/docs/installation/ 


## instalar opentelemetry operator

Esto va a ayudar a ocupar toda la logica de otel de forma facil.

```bash
kubectl apply -f https://github.com/open-telemetry/opentelemetry-operator/releases/latest/download/opentelemetry-operator.yaml
```

Paginas:
- https://opentelemetry.io/docs/platforms/kubernetes/operator/


## Instalando Jaeger para ver trazas

Instalando jaeger con helm desde https://github.com/jaegertracing/helm-charts/tree/v2

se puede ver mas de esto en la carpeta jaeger.

Para acceder por nodeport se puede ocupar la siguiente url http://192.168.88.103:30686/search

## Ahora a configurar el Collector

Creamos el namespace para el collector.

```bash
kubectl create ns collector
```

Para esto necesitamos el siguiente yaml:

```yaml
apiVersion: opentelemetry.io/v1beta1
kind: OpenTelemetryCollector
metadata:
  name: quotes 
  namespace: collector
spec:
  mode: daemonset
  config:
    receivers:
      otlp:
        protocols:
          grpc:
            endpoint: 0.0.0.0:4317
          http:
            endpoint: 0.0.0.0:4318

    exporters:
      debug:
        verbosity: detailed
      otlp/jaeger:
        endpoint: jaeger.jaeger.svc.cluster.local:4317
        tls:
          insecure: true

    service:
      pipelines:
        traces:
          receivers: [otlp]
          exporters: [debug, otlp/jaeger]
```


## Ahora la auto instrumentacion

Primero y principal deberiamos tener los dos namespaces de gateway-ns y quotes-ns.

Ahora, las dos apps son JAVA, por lo tanto vamos a tener que ocupar una autoinstrumentacion de JAVA.

Sigamos la siguiente pagina: https://opentelemetry.io/docs/platforms/kubernetes/operator/automatic/#java

```yaml
apiVersion: opentelemetry.io/v1alpha1
kind: Instrumentation
metadata:
  name: quotes-instrumentation
  namespace: collector
spec:
  exporter:
    endpoint: "http://quotes-collector.collector.svc.cluster.local:4318"
  propagators:
    - tracecontext
    - baggage
  sampler:
    type: parentbased_traceidratio
    argument: "1"
  env:
    - name: OTEL_JAVA_LOG_LEVEL
      value: "DEBUG"
    - name: OTEL_JAVA_DISABLE_LOGGING
      value: "false"
```

### Configurando los namespaces

Ahora debemos configurar los namespaces para que ocupen esta instrumentacion:

```
kubectl annotate namespace gateway-ns instrumentation.opentelemetry.io/inject-java=collector/quotes-instrumentation
```

```
kubectl annotate namespace quotes-ns instrumentation.opentelemetry.io/inject-java=collector/quotes-instrumentation
```

Asi como hicimos la configuracion a nivel del namespace, tambien podriamos hacer la configuracion a niver del template del pod de los deployments, cosa de que si queremos podriamos tener varios deploymentes de diferentes lenguajes en el mismo namespace.

## Ver en jaeger las trazas

Para ver las trazas que se fueron creando en jaeger, tenemos que acceder a la ui de jaeger (http://IP_DEL_SERVER:30686/search)

## Que vimos aqui?

Basicamente se instalo lo necesario para comenzar con la parte de trazas.

Se trabajo la auto instrumentacion de java para el servicio de frases.

Y se instalo jaeger para ver las trazas como van desde el gateway-api al quote-services
