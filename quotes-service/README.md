# Quotes Service

Este servicio gestiona una base de datos en memoria de frases inspiradoras.

## Descripción

Provee una API REST para crear, leer y borrar frases. Simula "trabajo pesado" con delays aleatorios para propósitos de prueba de observabilidad.

**Nota:** Este servicio corre en el puerto **8080** (interno del contenedor).

## Endpoints

- `GET /quotes`: Devuelve una frase aleatoria.
- `GET /quotes/all`: Devuelve todas las frases.
- `POST /quotes`: Crea una nueva frase.
  - Body: `{"autor": "...", "frase": "..."}`
- `DELETE /quotes/{id}`: Borra una frase por ID.
- `GET /quotes/health`: Chequeo de salud.

## Construcción y Ejecución

Para construir el proyecto con Gradle:

```bash
./gradlew build
```

Para correr el jar generado:

```bash
java -jar build/libs/quotes-service-0.0.1-SNAPSHOT.jar
```
