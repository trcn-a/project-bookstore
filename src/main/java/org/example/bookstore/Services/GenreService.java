package org.example.bookstore.Services;

import org.example.bookstore.Entities.Genre;
import org.example.bookstore.Repositories.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class GenreService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    public Genre getGenreById(Long genreId) {
        return genreRepository.findById(genreId)
                .orElseThrow(() -> new RuntimeException("Genre not found with ID: " + genreId));
    }

    public Genre createIfNotExists(String name) {

        return genreRepository.findByName(name)
                .orElseGet(() -> genreRepository.save(new Genre(name)));
    }

    public List<String> getAllGenreNames() {
        return genreRepository.findAllGenreNames();
    }
}
