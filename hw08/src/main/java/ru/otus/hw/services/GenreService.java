package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;

import ru.otus.hw.models.Genre;

public interface GenreService {
    List<Genre> findAll();

    Optional<Genre> findById(String id);

    Genre insert(String name);

    Genre update(String id, String name);

    void deleteById(String id);
}
