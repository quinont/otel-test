# Open Telemetry Test

Repositorio de demostración para probar Open Telemetry y orquestación con Kubernetes.
Contiene dos microservicios en Java (Spring Boot): `api-gateway` y `quotes-service`.

## Estructura del Proyecto

- `api-gateway/`: Servicio Gateway que orquesta peticiones.
- `quotes-service/`: Servicio backend que almacena y sirve frases.
- `k8s/`: Manifiestos de Kubernetes para desplegar la aplicación.
- `docker-compose.yml`: Configuración para despliegue local rápido.

## Ejecución Local con Docker Compose

Para levantar ambos servicios localmente utilizando Docker Compose:

1. Asegúrate de tener Docker y Docker Compose instalados.
2. Ejecuta el siguiente comando en la raíz del repositorio:

```bash
docker compose up --build -d
```

Esto levantará:
- **api-gateway** en `http://localhost:8080`
- **quotes-service** en `http://localhost:8081` (mapeado internamente al puerto 8080)

Puedes probar que todo funcione accediendo a:
`http://localhost:8080/resumen-inspirador`

Para detener los servicios:

```bash
docker compose down
```

## Despliegue en Kubernetes

Los manifiestos se encuentran en la carpeta `k8s/`. Se asume que tienes un clúster de Kubernetes corriendo y `kubectl` configurado.

### 1. Crear Namespaces

```bash
kubectl apply -f k8s/namespaces.yaml
```

### 2. Desplegar Quotes Service

```bash
kubectl apply -f k8s/quotes-deployment.yaml
```

Este despliegue crea:
- Un Deployment en el namespace `quotes-ns` con 2 réplicas.
- Un Service ClusterIP exponiendo el puerto 8080.

### 3. Desplegar API Gateway

```bash
kubectl apply -f k8s/gateway-deployment.yaml
```

Este despliegue crea:
- Un Deployment en el namespace `gateway-ns` con 2 réplicas.
- Un Service NodePort exponiendo el puerto 30080 (o un puerto aleatorio en el rango de NodePort, mapeado al 8080 del contenedor).

### Notas sobre Imágenes

Los manifiestos asumen que las imágenes `api-gateway:latest` y `quotes-service:latest` existen en el registro del clúster o son accesibles. Si estás usando Minikube o Kind, es posible que necesites cargar las imágenes manualmente o apuntar a un registro real.

Ejemplo para cargar imágenes en Kind:
```bash
kind load docker-image api-gateway:latest
kind load docker-image quotes-service:latest
```

## TODO list

- Configuración de collector y auto instrumentación con Open Telemetry Operator.
- Configuración de Prometheus, Jaeger, Loki y Grafana.
