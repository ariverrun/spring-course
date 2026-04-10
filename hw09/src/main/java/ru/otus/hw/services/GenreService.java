package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;

import ru.otus.hw.dto.CreateGenreRequestDto;
import ru.otus.hw.dto.UpdateGenreRequestDto;
import ru.otus.hw.models.Genre;

public interface GenreService {
    List<Genre> findAll();

    Optional<Genre> findById(long id);

    Genre insert(CreateGenreRequestDto dto);

    Genre update(long id, UpdateGenreRequestDto dto);

    void deleteById(long id);

    Genre getById(long id);
}
