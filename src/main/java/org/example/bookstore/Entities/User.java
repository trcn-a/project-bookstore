package org.example.bookstore.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

/**
 * Сутність, що представляє користувача системи.
 * Містить особисті дані користувача, такі як ім'я, прізвище, телефон, електронна пошта,
 * пароль та роль користувача в системі.
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * Унікальний ідентифікатор користувача.
     * Генерується автоматично.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Ім'я користувача.
     */
    @Column(name = "first_name")
    private String firstName;

    /**
     * Прізвище користувача.
     */
    private String lastName;

    /**
     * Номер телефону користувача.
     */
    private String phoneNumber;

    /**
     * Електронна пошта користувача.
     */
    private String email;

    /**
     * Пароль користувача.
     */
    private String password;

    /**
     * Роль користувача в системі (наприклад, "admin", "user").
     */
    private String role;

    /**
     * Конструктор за замовчуванням.
     */
    public User() { }

    /**
     * Повертає унікальний ідентифікатор користувача.
     *
     * @return ідентифікатор користувача
     */
    public Long getId() {
        return id;
    }

    /**
     * Встановлює ідентифікатор користувача.
     *
     * @param id ідентифікатор користувача
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Повертає ім'я користувача.
     *
     * @return ім'я користувача
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Встановлює ім'я користувача.
     *
     * @param firstName ім'я користувача
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Повертає прізвище користувача.
     *
     * @return прізвище користувача
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Встановлює прізвище користувача.
     *
     * @param lastName прізвище користувача
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Повертає номер телефону користувача.
     *
     * @return номер телефону
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Встановлює номер телефону користувача.
     *
     * @param phoneNumber номер телефону
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Повертає електронну пошту користувача.
     *
     * @return електронна пошта користувача
     */
    public String getEmail() {
        return email;
    }

    /**
     * Встановлює електронну пошту користувача.
     *
     * @param email електронна пошта
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Повертає пароль користувача.
     *
     * @return пароль користувача
     */
    public String getPassword() {
        return password;
    }

    /**
     * Встановлює пароль користувача.
     *
     * @param password пароль користувача
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Повертає роль користувача в системі.
     *
     * @return роль користувача
     */
    public String getRole() {
        return role;
    }

    /**
     * Встановлює роль користувача в системі.
     *
     * @param role роль користувача
     */
    public void setRole(String role) {
        this.role = role;
    }
}
