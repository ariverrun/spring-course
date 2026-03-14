package ru.otus.hw.repositories;

import java.util.List;
import java.util.Optional;

import ru.otus.hw.models.Author;

public interface AuthorRepository {
    List<Author> findAll();

    Optional<Author> findById(long id);

    Author save(Author author);

    void deleteById(long id);
}
