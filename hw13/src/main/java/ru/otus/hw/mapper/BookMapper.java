package ru.otus.hw.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Book;

@Component
public class BookMapper {
    public BookDto mapBookToDto(Book book) {
        return new BookDto(
            book.getId(), 
            book.getTitle(),
            new AuthorDto(
                book.getAuthor().getId(), 
                book.getAuthor().getFullName()
            ),
            book.getGenres().stream()
                .map(g -> new GenreDto(
                    g.getId(),
                    g.getName()
                ))
                .collect(Collectors.toSet())
        );
    }
}
