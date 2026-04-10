package ru.otus.hw.services;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dto.CreateBookRequestDto;
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
    public Optional<Book> findById(long id) {
        return bookRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        var books = bookRepository.findAll();
        initBooksLazyProperities(books);
        return books;
    }

    @Override
    @Transactional
    public Book insert(CreateBookRequestDto dto) {
        var book = new Book(
            null, 
            dto.title(), 
            getAuthorById(dto.authorId()), 
            getNotEmptyGenresListByIds(dto.genreIds())
        );
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book update(long id, UpdateBookRequestDto dto) {
        var book = bookRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Book with id %d is not found".formatted(id)));
        book.setTitle(dto.title());
        book.setAuthor(getAuthorById(dto.authorId()));
        book.setGenres(getNotEmptyGenresListByIds(dto.genreIds()));
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public Book getById(long id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(id)));
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

    private void initBooksLazyProperities(List<Book> books) {
        books.forEach(book -> book.getGenres().size());
    }
}
