# Revisando Open Telemetry

Basicamente tenemos un servicio que se conecta el gateway-api a un servicio llamado quote-services

El quote-services tiene unas frases simples, y las expone mediante una api, por el otro lado el gateway-api expone esta api para poder ser accedida desde afuera del cluster de kubernetes

# Cluster de Kubernetes

Para este ejemplo se ocupo k3s.

Los servicios estan expuestros mediantes nodeport, se podria implementar un ingress (o un gateway), pero como el nodeport fue mas simple fui por esa opcion.


# Que se va a tratar?

Basicamente trabajemos con el servicio de frases, e iremos sumando lentamente los tres pilares de la observabilidad de los sistemas, Trazas, metricas y logs.

La idea es ocupar autoinstrumentacion para las apps Java.

y por ahora tendremos Jaeger para las trazas, Prometheus para las metricas, y loki para el log.

Pero terminando esto, veremos como podemos cambiar el exporter para poder ocupar algo que este por fuera de estos productos internos del cluster.


