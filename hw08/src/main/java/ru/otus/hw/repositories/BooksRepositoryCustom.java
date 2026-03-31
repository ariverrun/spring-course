package ru.otus.hw.repositories;

import java.util.List;

import ru.otus.hw.models.Book;

public interface BooksRepositoryCustom {
    
    List<Book> findBooksWithGenre(String genreId);
}
