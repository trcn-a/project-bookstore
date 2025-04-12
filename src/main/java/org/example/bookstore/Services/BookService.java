package org.example.bookstore.Services;

import org.example.bookstore.Entities.Book;
import org.example.bookstore.Repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервісний клас для управління книгами в системі.
 * Включає методи для пошуку, фільтрації, сортування,
 * а також отримання книг за ID або автором.
 */

@Service
public class BookService {

    private final BookRepository bookRepository;

    /**
     * Конструктор для ініціалізації сервісу з репозиторієм книг.
     *
     * @param bookRepository Репозиторій для доступу до даних книг.
     */
    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Повертає певну сторінку книг з репозиторію, з заданою кількістю елементів на сторінці.
     *
     * @param page Номер сторінки.
     * @param size Кількість книг на сторінці.
     * @return Сторінка книг з репозиторію.
     */
    public Page<Book> getBooks(int page, int size) {
        return bookRepository.findAll(PageRequest.of(page, size));
    }

    /**
     * Виконує пошук книг за вказаним текстовим запитом.
     *
     * @param searchQuery Пошуковий запит для знаходження книг.
     * @return Список книг, що відповідають пошуковому запиту.
     */
    public List<Book> searchBooks(String searchQuery) {
        return bookRepository.searchBooks(searchQuery);
    }

    /**
     * Сортує книги за вказаним полем в порядку за зростанням або спаданням.
     *
     * @param sortBy Поле для сортування книг.
     * @param ascending Якщо true — сортує книги за зростанням, якщо false — за спаданням.
     * @param page Номер сторінки.
     * @param size Кількість книг на сторінці.
     * @return Сторінка книг, відсортованих за вказаним полем.
     */
    public Page<Book> getSortedBooks(String sortBy, boolean ascending, int page, int size) {
        Sort.Direction direction = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;
        return bookRepository.findAll(PageRequest.of(page, size, Sort.by(direction, sortBy)));
    }

    /**
     * Знаходить та повертає книгу за її унікальним ідентифікатором.
     *
     * @param id Ідентифікатор книги, яку потрібно знайти.
     * @return Книга з відповідним ідентифікатором.
     * @throws RuntimeException Якщо книга з таким ідентифікатором не знайдена.
     */
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

    /**
     * Фільтрує книги за авторами, жанрами, видавцями та ціною.
     *
     * @param authors Список авторів для фільтрації.
     * @param genres Список жанрів для фільтрації.
     * @param publishers Список видавців для фільтрації.
     * @param minPrice Мінімальна ціна для фільтрації.
     * @param maxPrice Максимальна ціна для фільтрації.
     * @return Список книг, що відповідають вказаним критеріям.
     * @throws IllegalArgumentException Якщо мінімальна ціна більша за максимальну.
     */
    public List<Book> filterBooks(List<String> authors, List<String> genres, List<String> publishers,
                                  Integer minPrice, Integer maxPrice) {

        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new IllegalArgumentException("Min price cannot be greater than max price.");
        }
        return bookRepository.filterBooks(authors, genres, publishers, minPrice, maxPrice);
    }

    /**
     * Знаходить всі книги, написані певним автором за його ідентифікатором.
     *
     * @param authorId Ідентифікатор автора, книги якого необхідно знайти.
     * @return Список книг, написаних автором з вказаним ідентифікатором.
     */
    public List<Book> getBooksByAuthor(Long authorId) {
        return bookRepository.findByAuthorId(authorId);
    }
}
