package org.example.bookstore.service;

import org.example.bookstore.entity.Genre;
import org.example.bookstore.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

/**
 * Сервісний клас для управління жанрами книг.
 */
public class GenreService {

    private final GenreRepository genreRepository;

    /**
     * Конструктор, який ініціалізує GenreService з використанням переданого GenreRepository.
     *
     * @param genreRepository Репозиторій для взаємодії з базою даних по жанрах.
     */
    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    /**
     * Повертає список усіх жанрів, що зберігаються у базі даних.
     *
     * @return Список усіх жанрів.
     */
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }



    /**
     * Створює новий жанр з вказаною назвою, якщо такого ще немає.
     * Якщо жанр з такою назвою вже існує, повертає існуючий.
     *
     * @param name Назва жанру для створення або пошуку.
     * @return Сутність жанру, що була створена або знайдена.
     */
    public Genre createIfNotExists(String name) {

        return genreRepository.findByName(name)
                .orElseGet(() -> genreRepository.save(new Genre(name)));
    }

    /**
     * Повертає список усіх назв жанрів, що зберігаються у базі даних.
     *
     * @return Список назв жанрів.
     */
    public List<String> getAllGenreNames() {
        return genreRepository.findAllGenreNames();
    }
}
