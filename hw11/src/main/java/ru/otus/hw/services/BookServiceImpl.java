package ru.otus.hw.services;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CreateBookRequestDto;
import ru.otus.hw.dto.UpdateBookRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    private final BookMapper bookMapper;

    @Override
    public Mono<BookDto> findById(String id) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Book with id %s not found".formatted(id))))
                .map(bookMapper::mapBookToDto);
    }

    @Override
    public Flux<BookDto> findAll() {
        return bookRepository.findAll()
                .map(bookMapper::mapBookToDto);
    }

    @Override
    public Mono<BookDto> insert(CreateBookRequestDto dto) {
        return validateAuthor(dto.authorId())
            .zipWith(validateGenres(dto.genreIds()))
            .flatMap(tuple -> {
                Author author = tuple.getT1();
                Set<Genre> genres = tuple.getT2();
                
                var book = new Book();
                book.setTitle(dto.title());
                book.setAuthor(author);
                book.setGenres(genres.stream().collect(Collectors.toList()));
                
                return bookRepository.save(book);
            })
            .map(bookMapper::mapBookToDto);
    }

    @Override
    public Mono<BookDto> update(String id, UpdateBookRequestDto dto) {
        return bookRepository.findById(id)
            .switchIfEmpty(Mono.error(new EntityNotFoundException("Book with id %s not found".formatted(id))))
            .zipWith(validateAuthor(dto.authorId()))
            .zipWith(validateGenres(dto.genreIds()))
            .flatMap(tuple -> {
                Book book = tuple.getT1().getT1();
                Author author = tuple.getT1().getT2();
                Set<Genre> genres = tuple.getT2();
                
                book.setTitle(dto.title());
                book.setAuthor(author);
                book.setGenres(genres.stream().collect(Collectors.toList()));
                
                return bookRepository.save(book);
            })
            .map(bookMapper::mapBookToDto);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return commentRepository.deleteByBookId(id)
            .then(bookRepository.deleteById(id));
    }

        private Mono<Author> validateAuthor(String authorId) {
        return authorRepository.findById(authorId)
            .switchIfEmpty(Mono.error(new EntityNotFoundException("Author with id %s not found".formatted(authorId))));
    }

    private Mono<Set<Genre>> validateGenres(Set<String> genreIds) {
        if (isEmpty(genreIds)) {
            return Mono.error(new IllegalArgumentException("Genres ids must not be null or empty"));
        }
        
        return genreRepository.findAllById(genreIds)
            .collect(Collectors.toSet())
            .flatMap(genres -> {
                if (genres.size() != genreIds.size()) {
                    return Mono.error(new EntityNotFoundException(
                        "One or all genres with ids %s not found".formatted(genreIds)));
                }
                return Mono.just(genres);
             });
    }
}