package org.example.bookstore.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

/**
 * Сутність, що представляє жанр книги.
 * Містить унікальний ідентифікатор та назву жанру.
 */
@Entity
@Table(name = "genres")
public class Genre {

    /**
     * Унікальний ідентифікатор жанру. Генерується автоматично.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Назва жанру книги.
     */
    private String name;

    /**
     * Конструктор без параметрів для створення екземплярів класу.
     */
    public Genre() {
    }

    /**
     * Повертає унікальний ідентифікатор жанру.
     *
     * @return ідентифікатор жанру
     */
    public Long getId() {
        return id;
    }

    /**
     * Встановлює ідентифікатор жанру.
     *
     * @param id ідентифікатор жанру
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Повертає назву жанру.
     *
     * @return назва жанру
     */
    public String getName() {
        return name;
    }

    /**
     * Встановлює назву жанру.
     *
     * @param name назва жанру
     */
    public void setName(String name) {
        this.name = name;
    }
}
