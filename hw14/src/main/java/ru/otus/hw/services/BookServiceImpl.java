package ru.otus.hw.services;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CreateBookRequestDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.UpdateBookRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.document.AuthorDocument;
import ru.otus.hw.models.document.BookDocument;
import ru.otus.hw.models.document.GenreDocument;
import ru.otus.hw.repositories.document.AuthorDocumentRepository;
import ru.otus.hw.repositories.document.BookDocumentRepository;
import ru.otus.hw.repositories.document.GenreDocumentRepository;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final AuthorDocumentRepository authorRepository;

    private final GenreDocumentRepository genreRepository;

    private final BookDocumentRepository bookRepository;

    @Override
    public BookDto findById(String id) {
        return bookRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s is not found".formatted(id)));
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public BookDto insert(CreateBookRequestDto dto) {
        AuthorDocument author = getAuthorById(dto.authorId());
        List<GenreDocument> genres = getNotEmptyGenresByIds(dto.genreIds());
        return mapToDto(bookRepository.save(new BookDocument(null, dto.title(), author, genres)));
    }

    @Override
    public BookDto update(String id, UpdateBookRequestDto dto) {
        BookDocument book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s is not found".formatted(id)));
        book.setTitle(dto.title());
        book.setAuthor(getAuthorById(dto.authorId()));
        book.setGenres(getNotEmptyGenresByIds(dto.genreIds()));
        return mapToDto(bookRepository.save(book));
    }

    @Override
    public void deleteById(String id) {
        bookRepository.deleteById(id);
    }

    private AuthorDocument getAuthorById(String authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(authorId)));
    }

    private List<GenreDocument> getNotEmptyGenresByIds(Set<String> genreIds) {
        if (isEmpty(genreIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }
        List<GenreDocument> genres = StreamSupport
                .stream(genreRepository.findAllById(genreIds).spliterator(), false)
                .toList();
        if (isEmpty(genres) || genreIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genreIds));
        }
        return genres;
    }

    private BookDto mapToDto(BookDocument book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                new AuthorDto(book.getAuthor().getId(), book.getAuthor().getFullName()),
                book.getGenres().stream()
                        .map(g -> new GenreDto(g.getId(), g.getName()))
                        .collect(Collectors.toSet())
        );
    }
}
