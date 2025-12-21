# Quotes service

Genera frases. Es el servicio de backend.

# Como generar la imagen

```
docker build -t quotes-service .
```

# Como ejecutar el container

```
docker run -d -p 8081:8081 quotes-service
```


# Para listar todas las frases

```
curl localhost:8081/quotes/all
```


# Para crear una nueva frase

```
curl localhost:8081/quotes -X POST --header "Content-Type: application/json" --data '{"autor": "yo", "frase": "prueba 123"}'
```


# Para eliminar una nueva frase

```
curl localhost:8081/quotes/3 -X DELETE --header "Content-Type: application/json"
```

