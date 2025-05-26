package org.example.bookstore.service;

import org.example.bookstore.entity.Publisher;
import org.example.bookstore.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class PublisherService {

    private final PublisherRepository publisherRepository;

    @Autowired
    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    public List<Publisher> findAll() {
        return publisherRepository.findAll();
    }

    public Publisher getPublisherById(Long publisherId) {
            return publisherRepository.findById(publisherId)
                    .orElseThrow(() -> new RuntimeException("Publisher not found with ID: " + publisherId));
        }

    public Publisher createIfNotExists(String name) {
        return publisherRepository.findByName(name)
                .orElseGet(() -> publisherRepository.save(new Publisher(name)));
    }

    public List<String> getAllPublisherNames() {
        return publisherRepository.findAllPublisherNames();
    }
}
