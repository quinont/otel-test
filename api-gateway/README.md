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

El servicio utiliza la variable de entorno `QUOTES_URL` para saber dónde conectar con el servicio de frases.
Por defecto apunta a `http://localhost:8081/quotes`.

## Construcción y Ejecución

Para construir el proyecto con Gradle:

```bash
./gradlew build
```

Para correr el jar generado:

```bash
java -jar build/libs/api-gateway-0.0.1-SNAPSHOT.jar
```
