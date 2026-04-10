package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;

import ru.otus.hw.dto.CreateBookRequestDto;
import ru.otus.hw.dto.UpdateBookRequestDto;
import ru.otus.hw.models.Book;

public interface BookService {
    Optional<Book> findById(long id);

    List<Book> findAll();

    Book insert(CreateBookRequestDto dto);

    Book update(long id, UpdateBookRequestDto dto);

    void deleteById(long id);

    Book getById(long id);
}
