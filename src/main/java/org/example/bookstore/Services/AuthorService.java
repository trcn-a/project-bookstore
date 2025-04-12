package org.example.bookstore.Services;

import org.example.bookstore.Entities.Author;
import org.example.bookstore.Repositories.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Автор не знайдений за ID: " + id));
    }
}
