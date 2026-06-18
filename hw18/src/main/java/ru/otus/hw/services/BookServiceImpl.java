package ru.otus.hw.services;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CreateBookRequestDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.UpdateBookRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    @Override
    @Retry(name = "bookService")
    @CircuitBreaker(name = "bookService", fallbackMethod = "findByIdFallback")
    @Transactional(readOnly = true)
    public BookDto findById(long id) {
        var book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d is not found".formatted(id)));
        return mapBookToDto(book);
    }

    @Override
    @Retry(name = "bookService")
    @CircuitBreaker(name = "bookService", fallbackMethod = "findAllFallback")
    @Transactional(readOnly = true)
    public List<BookDto> findAll() {
        var books = bookRepository.findAll();
        return books.stream()
            .map(book -> mapBookToDto(book))
            .toList();
    }

    @Override
    @CircuitBreaker(name = "bookService", fallbackMethod = "insertFallback")
    @Transactional
    public BookDto insert(CreateBookRequestDto dto) {
        var book = new Book(
            null,
            dto.title(),
            getAuthorById(dto.authorId()),
            getNotEmptyGenresListByIds(dto.genreIds())
        );
        return mapBookToDto(bookRepository.save(book));
    }

    @Override
    @Retry(name = "bookService")
    @CircuitBreaker(name = "bookService", fallbackMethod = "updateFallback")
    @Transactional
    public BookDto update(long id, UpdateBookRequestDto dto) {
        var book = bookRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Book with id %d is not found".formatted(id)));
        book.setTitle(dto.title());
        book.setAuthor(getAuthorById(dto.authorId()));
        book.setGenres(getNotEmptyGenresListByIds(dto.genreIds()));
        return mapBookToDto(bookRepository.save(book));
    }

    @Override
    @Retry(name = "bookService")
    @CircuitBreaker(name = "bookService", fallbackMethod = "deleteFallback")
    @Transactional
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }

    public List<BookDto> findAllFallback(Throwable ex) {
        return List.of();
    }

    public BookDto findByIdFallback(long id, Throwable ex) {
        throw new RuntimeException("Failed to find book by id %d".formatted(id), ex);
    }

    public BookDto insertFallback(CreateBookRequestDto dto, Throwable ex) {
        throw new RuntimeException("Failed to insert book", ex);
    }

    public BookDto updateFallback(long id, UpdateBookRequestDto dto, Throwable ex) {
        throw new RuntimeException("Failed to update book with id %d".formatted(id), ex);
    }

    public void deleteFallback(long id, Throwable ex) {
        throw new RuntimeException("Failed to delete book with id %d".formatted(id), ex);
    }

    private BookDto mapBookToDto(Book book) {
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

    private Author getAuthorById(long authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
    }

    private List<Genre> getNotEmptyGenresListByIds(Set<Long> genresIds) {
        if (isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }
        var genres = genreRepository.findAllByIds(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }
        return genres;        
    }
}
