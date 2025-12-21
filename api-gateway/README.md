# Api Gateway

Llama al servicio de frases, la idea es tener algun complejo sistema de generacion de frases...

# Como generar la imagen

```
docker build -t api-gateway .
```

# Como ejecutar el container

```
docker network create quotes
```

```
docker run -d -p 8081:8081 --name quotes-service --network quotes --network-alias quotes-service quotes-service
```

```
docker run -d -p 8080:8080 --name api-gateway --network quotes -e QUOTES_URL="http://quotes-service:8081/quotes" api-gateway
```

## NOTA

Para que funcione el resto de los comandos, debemos tener el servicio de frases arriba, de lo contrario, todo fallara.

# Para listar una frase random

```
curl localhost:8080/
```

# Para un "resumen inspirador"

Esto es solo para probocar "un ida y vuelta" entre el api y el quote service. La idea es simular un pensamiento pesado.

```
curl localhost:8080/resumen-inspirador
```


# Para guardar una frase

```
curl localhost:8080/guardar -X POST --header "Content-Type: application/json" --data '{"autor": "yo", "frase": "prueba 123"}'
```


# Para borrar una frase

```
curl localhost:8080/borrar/3 -X DELETE --header "Content-Type: application/json"
```

# Borrar todo el ambiente

```
docker rm -f api-gateway quotes-service
```

Ahora borramos el network
```
docker network rm quotes
```
