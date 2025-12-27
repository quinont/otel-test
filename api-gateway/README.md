# API Gateway

Este servicio actúa como un punto de entrada para la aplicación. Orquesta llamadas al backend de frases (quotes-service) para componer una respuesta.

## Descripción

El Gateway expone un endpoint principal `/resumen-inspirador` que realiza múltiples llamadas al servicio de quotes para simular una carga de trabajo y composición de datos.

## Endpoints

- `GET /resumen-inspirador`: Devuelve un objeto JSON con un título, tiempo de procesamiento y una colección de frases obtenidas del backend.
- `GET /`: Devuelve una frase aleatoria (proxy directo).
- `POST /guardar`: Guarda una nueva frase en el backend.
- `DELETE /borrar/{id}`: Elimina una frase en el backend.
- `GET /health`: Chequeo de salud.

## Configuración

El servicio utiliza la siguientes variables de entorno:

- `QUOTES_URL`: URL del servicio de frases. Por defecto apunta a `http://localhost:8081/quotes`.
- `LOG_LEVEL`: Nivel de detalle de los logs. Valores posibles: `INFO` (por defecto), `DEBUG`, `ERROR`, `WARN`, `TRACE`.

## Logging

El servicio cuenta con un sistema de logging que registra:
- **Requests Entrantes**: Método HTTP y URI.
- **Respuestas Salientes**: Estado HTTP y tiempo de procesamiento.
- **Eventos de Negocio**: Detalles sobre el flujo de ejecución.

El nivel de log puede ser ajustado dinámicamente mediante la variable de entorno `LOG_LEVEL`.

## Construcción y Ejecución

Para construir el proyecto con Gradle:

```bash
./gradlew build
```

Para correr el jar generado:

```bash
java -jar build/libs/api-gateway-0.0.1-SNAPSHOT.jar
```
