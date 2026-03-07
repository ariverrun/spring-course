package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;

import ru.otus.hw.models.Author;

public interface AuthorService {
    List<Author> findAll();

    Optional<Author> findById(long id);

    Author insert(String fullName);

    Author update(long id, String fullName);

    void deleteById(long id);
}
