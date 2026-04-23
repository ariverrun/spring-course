package ru.otus.hw.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Book;

@Component
public class BookMapper {
    public BookDto mapBookToDto(Book book) {
        AuthorDto authorDto = new AuthorDto(
            book.getAuthor().getId(),
            book.getAuthor().getFullName()
        );
        
        Set<GenreDto> genreDtos = book.getGenres().stream()
            .map(genre -> new GenreDto(genre.getId(), genre.getName()))
            .collect(Collectors.toSet());
        
        return new BookDto(
            book.getId(),
            book.getTitle(),
            authorDto,
            genreDtos
        );
    }
}
