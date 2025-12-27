package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
public class GatewayApplication {

    private static final Logger logger = LoggerFactory.getLogger(GatewayApplication.class);

    public static void main(String[] args) {
        System.setProperty("server.port", "8080");
        SpringApplication.run(GatewayApplication.class, args);
    }

    private final RestTemplate restTemplate;
    
    private final String backendUrl = System.getenv().getOrDefault("QUOTES_URL", "http://localhost:8081/quotes");

    public GatewayApplication() {
        this.restTemplate = new RestTemplate();
    }

    private void simulateProcessing(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @GetMapping("/resumen-inspirador")
    public Map<String, Object> getInspirationMix() {
        long start = System.currentTimeMillis();
        logger.info("Gateway: Procesando lógica de negocio...");
        simulateProcessing(500); 

        List<Object> frasesRecopiladas = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            logger.debug("Gateway: Llamando al backend iteración {}", i);
            Object frase = restTemplate.getForObject(backendUrl, Object.class);
            frasesRecopiladas.add(frase);
            simulateProcessing(200);
        }

        long totalTime = System.currentTimeMillis() - start;
        logger.info("Gateway: Procesamiento finalizado en {} ms", totalTime);

        return Map.of(
            "titulo", "Mix de Inspiración Semanal",
            "tiempo_procesamiento_gateway_ms", totalTime,
            "coleccion_frases", frasesRecopiladas
        );
    }
    
    @GetMapping("/")
    public Map<String, Object> getRandom() {
        return Map.of("data", restTemplate.getForObject(backendUrl, Object.class));
    }

    @PostMapping("/guardar")
    public Map<String, Object> save(@RequestBody Map<String, String> body) {
        Object response = restTemplate.postForObject(backendUrl, body, Object.class);
        return Map.of("status", "ok", "backend_response", response);
    }

    @DeleteMapping("/borrar/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        restTemplate.delete(backendUrl + "/" + id);
        return Map.of("status", "deleted", "id", id);
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }
}
