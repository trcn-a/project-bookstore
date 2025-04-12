package org.example.bookstore.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

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
    private String name;

    /**
     * Конструктор без параметрів.
     */
    public Author() { }

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
