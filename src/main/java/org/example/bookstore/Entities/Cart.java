package org.example.bookstore.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;

/**
 * Сутність, що представляє кошик користувача.
 * Містить унікальний ідентифікатор та зв'язок із користувачем.
 */
@Entity
@Table(name = "carts")
public class Cart {

    /**
     * Унікальний ідентифікатор кошика. Генерується автоматично.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Користувач, якому належить кошик.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Повертає унікальний ідентифікатор кошика.
     *
     * @return значення ідентифікатора
     */
    public Long getId() {
        return id;
    }

    /**
     * Встановлює ідентифікатор кошика.
     *
     * @param id значення ідентифікатора
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Повертає користувача, який володіє кошиком.
     *
     * @return об'єкт користувача
     */
    public User getUser() {
        return user;
    }

    /**
     * Встановлює користувача, якому належить кошик.
     *
     * @param user об'єкт користувача
     */
    public void setUser(User user) {
        this.user = user;
    }
}
