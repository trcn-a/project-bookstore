package org.example.bookstore.Services;

import org.example.bookstore.Entities.Author;
import org.example.bookstore.Entities.Publisher;
import org.example.bookstore.Repositories.AuthorRepository;
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
    private static final Logger logger = LoggerFactory.getLogger(AuthorService.class);

    /**
     * Конструктор, який ініціалізує AuthorService за допомогою переданого AuthorRepository.
     *
     * @param authorRepository Репозиторій, який використовується для взаємодії з базою даних для операцій,
     *                         що стосуються авторів.
     */
    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    /**
     * Отримує автора за його унікальним ідентифікатором (ID).
     * Якщо автор з вказаним ID не знайдений, викидається RuntimeException
     * з відповідним повідомленням про помилку.
     *
     * @param id Унікальний ідентифікатор автора, якого потрібно знайти.
     * @return Сутність Author, якщо вона знайдена.
     * @throws RuntimeException Якщо автор не знайдений за вказаним ID.
     */
    public Author getAuthorById(Long id) {
        logger.info("Request to retrieve author with ID: {}", id);

        try {
            Author author = authorRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Author not found with ID: " + id));
            logger.info("Author found: {}", author.getName());
            return author;
        } catch (RuntimeException e) {
            logger.error("Error: author with ID={} not found", id, e);
            throw e;
        }
    }

    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    public Author createIfNotExists(String name) {
        return authorRepository.findByName(name)
                .orElseGet(() -> authorRepository.save(new Author(name)));
    }

    public List<String> getAllAuthorNames() {
        return authorRepository.findAllAuthorNames();
    }
}
