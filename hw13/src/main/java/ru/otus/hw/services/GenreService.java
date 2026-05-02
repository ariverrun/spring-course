package ru.otus.hw.services;

import java.util.List;

import ru.otus.hw.dto.CreateGenreRequestDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.UpdateGenreRequestDto;

public interface GenreService {
    List<GenreDto> findAll();

    GenreDto findById(long id);

    GenreDto insert(CreateGenreRequestDto dto);

    GenreDto update(long id, UpdateGenreRequestDto dto);

    void deleteById(long id);
}
