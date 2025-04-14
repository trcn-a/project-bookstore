package org.example.bookstore;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Основний клас Spring Boot додатку, який запускає програму.
 * Використовує анотацію @SpringBootApplication для автоматичної конфігурації додатку.
 */
@SpringBootApplication
public class BookApplication {

    private static final Logger logger = LoggerFactory.getLogger(BookApplication.class);

    /**
     * Головний метод, який запускає Spring Boot додаток.
     *
     * @param args Аргументи командного рядка (не використовуються в даному випадку).
     */
    public static void main(String[] args) {
        logger.info("Starting Online Shop Application...");
        SpringApplication.run(BookApplication.class, args);
        logger.info("Online Shop Application Started Successfully.");

    }


    /**
     * Метод, що викликається перед завершенням роботи застосунку.
     *
     */
    @PreDestroy
    public void onShutdown() {
        logger.info("BookApplication is shutting down.");
    }
}