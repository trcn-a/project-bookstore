package org.example.bookstore.Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Сутність, що представляє знижку в системі.
 * Містить інформацію про відсоток знижки та період дії.
 */
@Entity
@Table(name = "discounts")
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Назва акції/знижки
     */
    private String name;

    /**
     * Відсоток знижки
     */
    @Column(name = "discount_percentage")
    private Integer discountPercentage;

    /**
     * Дата початку акції
     */
    @Column(name = "start_date")
    private LocalDateTime startDate;

    /**
     * Дата закінчення акції
     */
    @Column(name = "end_date")
    private LocalDateTime endDate;

    // Конструктор за замовчуванням
    public Discount() {
    }

    // Геттери та сеттери
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Integer discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
} 