package ru.otus.hw.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import ru.otus.hw.models.Genre;

public interface GenreRepository {
    List<Genre> findAll();

    List<Genre> findAllByIds(Set<Long> ids);

    Optional<Genre> findById(long id);

    Genre save(Genre genre);

    void deleteById(long id);
}
