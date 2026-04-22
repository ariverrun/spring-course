package ru.otus.hw.services;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public BookDto findById(long id) {
        var book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d is not found".formatted(id)));
        return mapBookToDto(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> findAll() {
        var books = bookRepository.findAll();
        return books.stream()
            .map(book -> mapBookToDto(book))
            .toList();
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

    @Override
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
    @Transactional
    public void deleteById(long id) {
        bookRepository.deleteById(id);
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
