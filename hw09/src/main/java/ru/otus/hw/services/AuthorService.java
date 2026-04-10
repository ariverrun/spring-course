package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;

import ru.otus.hw.dto.CreateAuthorRequestDto;
import ru.otus.hw.dto.UpdateAuthorRequestDto;
import ru.otus.hw.models.Author;

public interface AuthorService {
    List<Author> findAll();

    Optional<Author> findById(long id);

    Author insert(CreateAuthorRequestDto dto);

    Author update(long id, UpdateAuthorRequestDto dto);

    void deleteById(long id);

    Author getById(long id);
}
