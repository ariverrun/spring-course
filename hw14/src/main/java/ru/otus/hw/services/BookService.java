package ru.otus.hw.services;

import java.util.List;

import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CreateBookRequestDto;
import ru.otus.hw.dto.UpdateBookRequestDto;

public interface BookService {
    BookDto findById(String id);

    List<BookDto> findAll();

    BookDto insert(CreateBookRequestDto dto);

    BookDto update(String id, UpdateBookRequestDto dto);

    void deleteById(String id);
}
