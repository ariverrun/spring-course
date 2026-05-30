package ru.otus.hw.services;

import java.util.List;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.CreateAuthorRequestDto;
import ru.otus.hw.dto.UpdateAuthorRequestDto;

public interface AuthorService {
    List<AuthorDto> findAll();

    AuthorDto findById(String id);

    AuthorDto insert(CreateAuthorRequestDto dto);

    AuthorDto update(String id, UpdateAuthorRequestDto dto);

    void deleteById(String id);
}
