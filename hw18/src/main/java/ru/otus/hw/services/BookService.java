package ru.otus.hw.services;

import java.util.List;

import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CreateBookRequestDto;
import ru.otus.hw.dto.UpdateBookRequestDto;

public interface BookService {
    BookDto findById(long id);

    List<BookDto> findAll();

    BookDto insert(CreateBookRequestDto dto);

    BookDto update(long id, UpdateBookRequestDto dto);

    void deleteById(long id);
}
