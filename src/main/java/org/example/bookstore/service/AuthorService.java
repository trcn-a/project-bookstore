package org.example.bookstore.service;

import org.example.bookstore.entity.Author;
import org.example.bookstore.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Сервісний клас для управління авторами в системі.
 * Включає методи для отримання та управління сутностями авторів.
 */
@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    /**
     * Конструктор, який ініціалізує AuthorService за допомогою переданого AuthorRepository.
     *
     * @param authorRepository Репозиторій для взаємодії з базою даних для операцій,
     *                         що стосуються авторів.
     */
    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    /**
     * Знаходить автора за його унікальним ідентифікатором (ID).
     *
     * @param id Унікальний ідентифікатор автора, якого потрібно знайти.
     * @return Сутність Author, якщо вона знайдена.
     * @throws RuntimeException Якщо автор не знайдений за вказаним ID.
     */
    public Author getAuthorById(Long id) {

        try {
            Author author = authorRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Author not found with ID: " + id));
            return author;
        } catch (RuntimeException e) {
            throw e;
        }
    }

    public double getAuthorAverageRating(Long authorId) {
        return authorRepository.findAverageRatingByAuthorId(authorId).orElse(0.0);
    }
    public int getAuthorCountRating(Long authorId) {
        return authorRepository.findReviewCountByAuthorId(authorId).orElse(0);
    }

    /**
     * Повертає список усіх авторів у системі.
     *
     * @return список усіх авторів.
     */
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    /**
     * Створює нового автора з вказаним ім'ям, якщо такий ще не існує.
     *
     * @param name ім'я автора.
     * @return існуючий або створений автор
     */
    public Author createIfNotExists(String name) {
        return authorRepository.findByName(name)
                .orElseGet(() -> authorRepository.save(new Author(name)));
    }

    /**
     * Повертає список імен усіх авторів.
     *
     * @return список імен авторів.
     */
    public List<String> getAllAuthorNames() {
        return authorRepository.findAllAuthorNames();
    }
}
