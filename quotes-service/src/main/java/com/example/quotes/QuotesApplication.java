package com.example.quotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
@RestController
@RequestMapping("/quotes")
public class QuotesApplication {
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
            System.out.println("Simulando trabajo pesado: " + delay + "ms");
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @GetMapping
    public Quote getRandomQuote() {
        if (db.isEmpty()) return new Quote(0L, "System", "No hay frases");
        return db.get(new Random().nextInt(db.size()));
    }

    @PostMapping
    public Quote createQuote(@RequestBody Map<String, String> body) {
        simulateHeavyWork();

        Quote newQuote = new Quote(
            idCounter.getAndIncrement(),
            body.get("autor"),
            body.get("frase")
        );
        db.add(newQuote);
        return newQuote;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteQuote(@PathVariable Long id) {
        simulateHeavyWork();

        boolean removed = db.removeIf(q -> q.id().equals(id));
        
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
