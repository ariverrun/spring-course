package ru.otus.hw.services;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CreateBookRequestDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.UpdateBookRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
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

    @Override
    public Mono<BookDto> findById(String id) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Book with id %s not found".formatted(id))))
                .flatMap(this::mapBookToDto);
    }

    @Override
    public Flux<BookDto> findAll() {
        return bookRepository.findAll()
                .flatMap(this::mapBookToDto);
    }

    @Override
    public Mono<BookDto> insert(CreateBookRequestDto dto) {
        return validateAuthor(dto.authorId())
            .zipWith(validateGenres(dto.genreIds()))
            .flatMap(tuple -> {
                var book = new Book();
                book.setTitle(dto.title());
                
                return authorRepository.findById(dto.authorId())
                    .flatMap(author -> {
                        book.setAuthor(author);
                        return genreRepository.findAllById(dto.genreIds()).collectList();
                    })
                    .flatMap(genres -> {
                        book.setGenres(genres);
                        return bookRepository.save(book);
                    });
            })
            .flatMap(this::mapBookToDto);
    }

    @Override
    public Mono<BookDto> update(String id, UpdateBookRequestDto dto) {
        return bookRepository.findById(id)
            .switchIfEmpty(Mono.error(new EntityNotFoundException("Book with id %s not found".formatted(id))))
            .zipWith(validateAuthor(dto.authorId()))
            .zipWith(validateGenres(dto.genreIds()))
            .flatMap(tuple -> {
                Book book = tuple.getT1().getT1();
                book.setTitle(dto.title());
                
                return authorRepository.findById(dto.authorId())
                    .flatMap(author -> {
                        book.setAuthor(author);
                        return genreRepository.findAllById(dto.genreIds()).collectList();
                    })
                    .flatMap(genres -> {
                        book.setGenres(genres);
                        return bookRepository.save(book);
                    });
            })
            .flatMap(this::mapBookToDto);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return commentRepository.deleteByBookId(id)
            .then(bookRepository.deleteById(id));
    }

    private Mono<AuthorDto> getAuthorById(String authorId) {
        return authorRepository.findById(authorId)
            .switchIfEmpty(Mono.error(new EntityNotFoundException("Author with id %s not found".formatted(authorId))))
            .map(author -> new AuthorDto(author.getId(), author.getFullName()));
    }

    private Mono<Set<GenreDto>> getGenresByIds(Set<String> genreIds) {
        if (isEmpty(genreIds)) {
            return Mono.just(Set.of());
        }
        
        return genreRepository.findAllById(genreIds)
            .map(genre -> new GenreDto(genre.getId(), genre.getName()))
            .collect(Collectors.toSet());
    }

    private Mono<AuthorDto> validateAuthor(String authorId) {
        return authorRepository.findById(authorId)
            .switchIfEmpty(Mono.error(new EntityNotFoundException("Author with id %s not found".formatted(authorId))))
            .map(author -> new AuthorDto(author.getId(), author.getFullName()));
    }

    private Mono<Set<GenreDto>> validateGenres(Set<String> genreIds) {
        if (isEmpty(genreIds)) {
            return Mono.error(new IllegalArgumentException("Genres ids must not be null or empty"));
        }
        
        return genreRepository.findAllById(genreIds)
            .map(genre -> new GenreDto(genre.getId(), genre.getName()))
            .collect(Collectors.toSet())
            .flatMap(genres -> {
                if (genres.size() != genreIds.size()) {
                    return Mono.error(new EntityNotFoundException(
                        "One or all genres with ids %s not found".formatted(genreIds)));
                }
                return Mono.just(genres);
            });
    }

    private Mono<BookDto> mapBookToDto(Book book) {
        return Mono.zip(
            getAuthorById(book.getAuthor().getId()),
            getGenresByIds(book.getGenres().stream()
                .map(genre -> genre.getId())
                .collect(Collectors.toSet()))
        ).map(tuple -> {
            AuthorDto author = tuple.getT1();
            Set<GenreDto> genres = tuple.getT2();
            
            return new BookDto(
                book.getId(),
                book.getTitle(),
                author,
                genres
            );
        });
    }    
}