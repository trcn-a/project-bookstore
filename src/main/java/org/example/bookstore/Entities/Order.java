package org.example.bookstore.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;

import java.time.LocalDateTime;

/**
 * Сутність, що представляє замовлення користувача.
 * Містить інформацію про користувача, статус замовлення, дані для доставки та інші деталі.
 */
@Entity
@Table(name = "orders")
public class Order {

    /**
     * Унікальний ідентифікатор замовлення. Генерується автоматично.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Користувач, який зробив замовлення.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Номер телефону користувача для доставки.
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * Ім'я користувача.
     */
    @Column(name = "first_name")
    private String firstName;

    /**
     * Прізвище користувача.
     */
    @Column(name = "last_name")
    private String lastName;

    /**
     * Місто, в яке буде здійснена доставка.
     */
    @Column(name = "city")
    private String city;

    /**
     * Номер відділення пошти для доставки.
     */
    @Column(name = "post_office_number")
    private String postOfficeNumber;

    /**
     * Статус замовлення (наприклад, обробка, доставлено).
     */
    private String status;

    /**
     * Загальна сума замовлення.
     */
    @Column(name = "total_amount")
    private Integer totalAmount;

    /**
     * Номер для відстеження замовлення.
     */
    @Column(name = "tracking_number")
    private String trackingNumber;

    /**
     * Дата та час створення замовлення.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Дата та час останнього оновлення замовлення.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Повертає унікальний ідентифікатор замовлення.
     *
     * @return ідентифікатор замовлення
     */
    public Long getId() {
        return id;
    }

    /**
     * Встановлює ідентифікатор замовлення.
     *
     * @param id ідентифікатор замовлення
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Повертає користувача, який зробив замовлення.
     *
     * @return об'єкт користувача
     */
    public User getUser() {
        return user;
    }

    /**
     * Встановлює користувача, який зробив замовлення.
     *
     * @param user об'єкт користувача
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Повертає номер телефону користувача для доставки.
     *
     * @return номер телефону
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Встановлює номер телефону користувача для доставки.
     *
     * @param phoneNumber номер телефону
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
     * Повертає місто для доставки.
     *
     * @return місто
     */
    public String getCity() {
        return city;
    }

    /**
     * Встановлює місто для доставки.
     *
     * @param city місто
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Повертає номер поштового відділення для доставки.
     *
     * @return номер поштового відділення
     */
    public String getPostOfficeNumber() {
        return postOfficeNumber;
    }

    /**
     * Встановлює номер поштового відділення для доставки.
     *
     * @param postOfficeNumber номер поштового відділення
     */
    public void setPostOfficeNumber(String postOfficeNumber) {
        this.postOfficeNumber = postOfficeNumber;
    }

    /**
     * Повертає статус замовлення (наприклад, обробка, доставлено).
     *
     * @return статус замовлення
     */
    public String getStatus() {
        return status;
    }

    /**
     * Встановлює статус замовлення.
     *
     * @param status статус замовлення
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Повертає загальну суму замовлення.
     *
     * @return сума замовлення
     */
    public Integer getTotalAmount() {
        return totalAmount;
    }

    /**
     * Встановлює загальну суму замовлення.
     *
     * @param totalAmount сума замовлення
     */
    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Повертає номер для відстеження замовлення.
     *
     * @return номер для відстеження
     */
    public String getTrackingNumber() {
        return trackingNumber;
    }

    /**
     * Встановлює номер для відстеження замовлення.
     *
     * @param trackingNumber номер для відстеження
     */
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    /**
     * Повертає дату та час створення замовлення.
     *
     * @return дата та час створення
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Встановлює дату та час створення замовлення.
     *
     * @param createdAt дата та час створення
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Повертає дату та час останнього оновлення замовлення.
     *
     * @return дата та час оновлення
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Встановлює дату та час останнього оновлення замовлення.
     *
     * @param updatedAt дата та час оновлення
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
