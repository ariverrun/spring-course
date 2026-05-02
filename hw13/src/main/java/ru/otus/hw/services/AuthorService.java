package ru.otus.hw.services;

import java.util.List;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.CreateAuthorRequestDto;
import ru.otus.hw.dto.UpdateAuthorRequestDto;

public interface AuthorService {
    List<AuthorDto> findAll();

    AuthorDto findById(long id);

    AuthorDto insert(CreateAuthorRequestDto dto);

    AuthorDto update(long id, UpdateAuthorRequestDto dto);

    void deleteById(long id);
}
