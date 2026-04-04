package ru.otus.hw.services;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.exceptions.EntityNotFoundException;
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

    @Override
    public Optional<Book> findById(String id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<Book> findAll() {
        return Streamable.of(bookRepository.findAll()).toList();
    }

    @Override
    public Book insert(String title, String authorId, Set<String> genresIds) {
        var book = new Book(null, title, getAuthorById(authorId), getNotEmptyGenresListByIds(genresIds));
        return bookRepository.save(book);
    }

    @Override
    public Book update(String id, String title, String authorId, Set<String> genresIds) {
        var book = bookRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Book with id %s is not found".formatted(id)));
        book.setTitle(title);
        book.setAuthor(getAuthorById(authorId));
        book.setGenres(getNotEmptyGenresListByIds(genresIds));
        return bookRepository.save(book);
    }

    @Override
    public void deleteById(String id) {
        var book = bookRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Book with id %s is not found".formatted(id)));
        commentRepository.deleteByBook(book);
        bookRepository.deleteById(id);
    }

    private Author getAuthorById(String authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(authorId)));
    }

    private List<Genre> getNotEmptyGenresListByIds(Set<String> genresIds) {
        if (isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }
        
        Iterable<Genre> genresIterable = genreRepository.findAllById(genresIds);
        List<Genre> genres = StreamSupport.stream(genresIterable.spliterator(), false)
                                        .collect(Collectors.toList());
        
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }
        return genres;
    }
}
