package ru.otus.hw.services;

import java.util.List;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.CreateAuthorRequestDto;
import ru.otus.hw.dto.UpdateAuthorRequestDto;
import ru.otus.hw.models.Author;

public interface AuthorService {
    List<Author> findAll();

    AuthorDto findById(long id);

    AuthorDto insert(CreateAuthorRequestDto dto);

    AuthorDto update(long id, UpdateAuthorRequestDto dto);

    void deleteById(long id);
}
