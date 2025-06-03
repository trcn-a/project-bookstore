package org.example.bookstore.entity;

import jakarta.persistence.*;

/**
 * Сутність, що представляє автора у системі онлайн-книгарні.
 * Містить унікальний ідентифікатор та ім'я автора.
 */
@Entity
@Table(name = "authors")
public class Author {

    /**
     * Унікальний ідентифікатор автора. Генерується автоматично.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Ім'я автора.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Конструктор без параметрів.
     */
    public Author(String name) {
        this.name = name;
    }

    public Author() {

    }

    /**
     * Повертає унікальний ідентифікатор автора.
     *
     * @return значення ідентифікатора
     */
    public Long getId() {
        return id;
    }

    /**
     * Встановлює ідентифікатор автора.
     *
     * @param id значення ідентифікатора
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Повертає ім'я автора.
     *
     * @return ім'я автора
     */
    public String getName() {
        return name;
    }

    /**
     * Встановлює ім'я автора.
     *
     * @param name ім'я автора
     */
    public void setName(String name) {
        this.name = name;
    }
}
