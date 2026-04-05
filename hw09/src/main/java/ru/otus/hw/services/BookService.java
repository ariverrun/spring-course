package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import ru.otus.hw.models.Book;

public interface BookService {
    Optional<Book> findById(long id);

    List<Book> findAll();

    Book insert(String title, long authorId, Set<Long> genresIds);

    Book update(long id, String title, long authorId, Set<Long> genresIds);

    void deleteById(long id);

    Book getById(long id);
}
