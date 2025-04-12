package org.example.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Основний клас Spring Boot додатку, який запускає програму.
 * Використовує анотацію @SpringBootApplication для автоматичної конфігурації додатку.
 */
@SpringBootApplication
public class BookApplication {

    /**
     * Головний метод, який запускає Spring Boot додаток.
     *
     * @param args Аргументи командного рядка (не використовуються в даному випадку).
     */
    public static void main(String[] args) {
        SpringApplication.run(BookApplication.class, args);
    }
}
