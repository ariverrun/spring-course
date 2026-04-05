package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;

import ru.otus.hw.models.Genre;

public interface GenreService {
    List<Genre> findAll();

    Optional<Genre> findById(long id);

    Genre insert(String name);

    Genre update(long id, String name);

    void deleteById(long id);

    Genre getById(long id);
}
