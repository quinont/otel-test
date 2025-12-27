package com.example.quotes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
@RestController
@RequestMapping("/quotes")
public class QuotesApplication {

    private static final Logger logger = LoggerFactory.getLogger(QuotesApplication.class);

    public static void main(String[] args) {
        System.setProperty("server.port", "8080");
        SpringApplication.run(QuotesApplication.class, args);
    }

    public record Quote(Long id, String autor, String frase) {}

    private final List<Quote> db = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public QuotesApplication() {
        db.add(new Quote(idCounter.getAndIncrement(), "Yoda", "Hazlo o no lo hagas."));
        db.add(new Quote(idCounter.getAndIncrement(), "Terminator", "Volveré."));
    }

    private void simulateHeavyWork() {
        try {
            // Delay aleatorio entre 2000ms (2s) y 4000ms (4s)
            long delay = 2000 + new Random().nextInt(2000);
            logger.info("Simulando trabajo pesado: {}ms", delay);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @GetMapping
    public Quote getRandomQuote() {
        logger.debug("Retrieving random quote");
        if (db.isEmpty()) return new Quote(0L, "System", "No hay frases");
        return db.get(new Random().nextInt(db.size()));
    }

    @PostMapping
    public Quote createQuote(@RequestBody Map<String, String> body) {
        logger.info("Creating new quote for author: {}", body.get("autor"));
        simulateHeavyWork();

        Quote newQuote = new Quote(
            idCounter.getAndIncrement(),
            body.get("autor"),
            body.get("frase")
        );
        db.add(newQuote);
        logger.debug("Quote created with ID: {}", newQuote.id());
        return newQuote;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteQuote(@PathVariable Long id) {
        logger.info("Request to delete quote with ID: {}", id);
        simulateHeavyWork();

        boolean removed = db.removeIf(q -> q.id().equals(id));
        if (removed) {
            logger.info("Quote with ID: {} deleted successfully", id);
        } else {
            logger.warn("Quote with ID: {} not found for deletion", id);
        }
        
        return removed ? Map.of("status", "eliminado", "id", id.toString()) 
                       : Map.of("status", "no encontrado");
    }
    
    @GetMapping("/all")
    public List<Quote> getAll() {
        return db;
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }
}
