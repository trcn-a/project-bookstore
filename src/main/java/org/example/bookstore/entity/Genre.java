package org.example.bookstore.entity;

import jakarta.persistence.*;

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
    @Column(nullable = false)
    private String name;

    /**
     * Конструктор без параметрів для створення екземплярів класу.
     */
    public Genre() {
    }

    public Genre(String name) {
        this.name = name;
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
