package org.example.bookstore.entity;

import jakarta.persistence.*;

/**
 * Сутність, що представляє видавця книги.
 * Містить інформацію про видавця, включаючи його унікальний ідентифікатор та назву.
 */
@Entity
@Table(name = "publishers")
public class Publisher {

    /**
     * Унікальний ідентифікатор видавця.
     * Генерується автоматично.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Назва видавця.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Конструктор за замовчуванням.
     */
    public Publisher() { }

    public Publisher(String name) {
        this.name=name;
    }

    /**
     * Повертає унікальний ідентифікатор видавця.
     *
     * @return ідентифікатор видавця
     */
    public Long getId() {
        return id;
    }

    /**
     * Встановлює ідентифікатор видавця.
     *
     * @param id ідентифікатор видавця
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Повертає назву видавця.
     *
     * @return назва видавця
     */
    public String getName() {
        return name;
    }

    /**
     * Встановлює назву видавця.
     *
     * @param name назва видавця
     */
    public void setName(String name) {
        this.name = name;
    }
}
