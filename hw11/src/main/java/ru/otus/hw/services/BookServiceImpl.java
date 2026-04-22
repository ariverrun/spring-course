package ru.otus.hw.services;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
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
    public Mono<BookDto> findById(long id) {
        return Mono.fromCallable(() -> bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(id))))
                .map(this::mapBookToDto)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<BookDto> findAll() {
        return Mono.fromCallable(bookRepository::findAll)
                .flatMapMany(Flux::fromIterable)
                .map(this::mapBookToDto)
                .subscribeOn(Schedulers.boundedElastic());
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
    public Mono<BookDto> insert(CreateBookRequestDto dto) {
        return Mono.zip(
            getAuthorById(dto.authorId()),
            getGenresByIds(dto.genreIds())
        )
        .flatMap(tuple -> {
            Author author = tuple.getT1();
            List<Genre> genres = tuple.getT2();
            
            return Mono.fromCallable(() -> {
                var book = new Book(null, dto.title(), author, genres);
                return bookRepository.save(book);
            });
        })
        .map(this::mapBookToDto)
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
        public Mono<BookDto> update(long id, UpdateBookRequestDto dto) {
        return Mono.zip(
            Mono.fromCallable(() -> bookRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Book with id %d is not found".formatted(id)))),
            getAuthorById(dto.authorId()),
            getGenresByIds(dto.genreIds())
        )
        .flatMap(tuple -> {
            Book book = tuple.getT1();
            Author author = tuple.getT2();
            List<Genre> genres = tuple.getT3();
            
            book.setTitle(dto.title());
            book.setAuthor(author);
            book.setGenres(genres);
            
            return Mono.fromCallable(() -> bookRepository.save(book));
        })
        .map(this::mapBookToDto)
        .subscribeOn(Schedulers.boundedElastic());
}

    @Override
    @Transactional
    public Mono<Void> deleteById(long id) {
        return Mono.fromRunnable(() -> bookRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private Mono<Author> getAuthorById(long authorId) {
        return Mono.fromCallable(() -> authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId))))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<List<Genre>> getGenresByIds(Set<Long> genresIds) {
        if (isEmpty(genresIds)) {
            return Mono.error(new IllegalArgumentException("Genres ids must not be null"));
        }
        
        return Mono.fromCallable(() -> genreRepository.findAllByIds(genresIds))
            .flatMap(genres -> {
                if (isEmpty(genres) || genresIds.size() != genres.size()) {
                    return Mono.error(new EntityNotFoundException(
                        "One or all genres with ids %s not found".formatted(genresIds)));
                }
                return Mono.just(genres);
            })
            .subscribeOn(Schedulers.boundedElastic());
    }
}
