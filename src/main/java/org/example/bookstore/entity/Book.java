package org.example.bookstore.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;

/**
 * Сутність, що представляє книгу в системі.
 * Відповідає таблиці books у БД.
 */
@Entity
@Table(name = "books")
@Check(constraints = "pages > 0")
@Check(constraints = "price > 0")
@Check(constraints = "stock_quantity >= 0")
@Check(constraints = "publication_year >= 2000 AND publication_year <= extract(year from current_date)")
@Check(constraints = "discount >= 0 and discount < 100")
@Check(constraints = "cover_type IN ('М''яка', 'Тверда', 'Суперобкладинка')")

public class Book {

    /**
     * Унікальний ідентифікатор книги. Генерується автоматично.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Назва книги
     */
    @Column(nullable = false)
    private String title;

    /**
     * Автор книги
     */
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    /**
     * Жанр книги
     */
    @ManyToOne
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;


    /**
     * Анотація книги
     */
    @Column(length = 1000, nullable = false)

    private String description;

    /**
     * ISBN
     */
    @Column(length = 20, nullable = false)
    private String isbn;

    /**
     * Ціна
     */
    @Column(name="price", nullable = false)

    private Integer price;


    /**
     * Знижка у відсотках
     */
    @Column(name="discount",  nullable = false, columnDefinition = "integer default 0")
    private Integer discount;

    /**
     * Видавництво
     */
    @ManyToOne
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher;

    /**
     * Кількість примірників книги на складі
     */
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    /**
     * Шлях до зображення обкладинки
     */
    @Column(name = "cover_image", nullable = false)
    private String coverImage;

    /**
     * Кількість сторінок
     */
    @Column(name="pages", nullable = false)

    private Integer pages;

    /**
     * Рік публікації
     */
    @Column(name = "publication_year", nullable = false)
    private Integer publicationYear;

    /**
     * Тип обкладинки
     */
    @Column(name = "cover_type", nullable = false, length = 20)
    private String coverType;


    /**
     * Конструктор за замовчуванням
     */
    public Book() {
    }

    /**
     * Повертає актуальну ціну, враховуючи знижку
     *
     * @return ціна зі знижкою
     */
    public Integer getActualPrice() {
        return price - (price * discount / 100);
    }

    /**
     * Повертає унікальний ідентифікатор книги.
     *
     * @return ідентифікатор книги.
     */
    public Long getId() {
        return id;
    }

    /**
     * Встановлює унікальний ідентифікатор книги.
     *
     * @param id унікальний ідентифікатор книги.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Повертає назву книги.
     *
     * @return назва книги.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Встановлює назву книги.
     *
     * @param title назва книги.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Повертає автора книги.
     *
     * @return автор книги.
     */
    public Author getAuthor() {
        return author;
    }

    /**
     * Встановлює автора книги.
     *
     * @param author автор книги.
     */
    public void setAuthor(Author author) {
        this.author = author;
    }

    /**
     * Повертає жанр книги.
     *
     * @return жанр книги.
     */
    public Genre getGenre() {
        return genre;
    }

    /**
     * Встановлює жанр книги.
     *
     * @param genre жанр книги.
     */
    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    /**
     * Повертає опис книги.
     *
     * @return опис книги.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Встановлює опис книги.
     *
     * @param description опис книги.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Повертає ISBN книги.
     *
     * @return ISBN книги.
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Встановлює ISBN книги.
     *
     * @param isbn ISBN книги.
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Повертає стандартну ціну книги.
     *
     * @return стандартна ціна книги.
     */
    public Integer getPrice() {
        return price;
    }

    /**
     * Встановлює стандартну ціну книги.
     *
     * @param price стандартна ціна книги.
     */
    public void setPrice(Integer price) {
        this.price = price;
    }


    /**
     * Повертає відсоток знижки на книгу.
     *
     * @return відсоток знижки.
     */
    public Integer getDiscount() {
        return discount;
    }

    /**
     * Встановлює відсоток знижки на книгу.
     *
     * @param discount відсоток знижки.
     */
    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    /**
     * Повертає видавця книги.
     *
     * @return видавець книги.
     */
    public Publisher getPublisher() {
        return publisher;
    }

    /**
     * Встановлює видавця книги.
     *
     * @param publisher видавець книги.
     */
    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    /**
     * Повертає кількість примірників книги на складі.
     *
     * @return кількість примірників на складі.
     */
    public Integer getStockQuantity() {
        return stockQuantity;
    }

    /**
     * Встановлює кількість примірників книги на складі.
     *
     * @param stockQuantity кількість примірників на складі.
     */
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    /**
     * Повертає шлях до зображення обкладинки книги.
     *
     * @return шлях до зображення обкладинки.
     */
    public String getCoverImage() {
        return coverImage;
    }

    /**
     * Встановлює шлях до зображення обкладинки книги.
     *
     * @param coverImage шлях до зображення обкладинки.
     */
    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    /**
     * Повертає кількість сторінок у книзі.
     *
     * @return кількість сторінок.
     */
    public Integer getPages() {
        return pages;
    }

    /**
     * Встановлює кількість сторінок у книзі.
     *
     * @param pages кількість сторінок.
     */
    public void setPages(Integer pages) {
        this.pages = pages;
    }

    /**
     * Повертає рік публікації книги.
     *
     * @return рік публікації.
     */
    public Integer getPublicationYear() {
        return publicationYear;
    }

    /**
     * Встановлює рік публікації книги.
     *
     * @param publicationYear рік публікації книги.
     */
    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    /**
     * Повертає тип обкладинки книги.
     *
     * @return тип обкладинки.
     */
    public String getCoverType() {
        return coverType;
    }

    /**
     * Встановлює тип обкладинки книги.
     *
     * @param coverType тип обкладинки.
     */
    public void setCoverType(String coverType) {
        this.coverType = coverType;
    }


}


