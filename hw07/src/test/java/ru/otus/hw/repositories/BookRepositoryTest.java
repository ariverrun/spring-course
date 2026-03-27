package ru.otus.hw.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    private BookRepository repository;

    @Autowired
    private TestEntityManager em;

    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldFindExpectedBookById(Book expectedBook) {
        var optionalActualBook = repository.findById(expectedBook.getId());
        assertThat(optionalActualBook).isPresent().get()
            .isEqualTo(expectedBook);
    }

    @Test
    void shouldFindAllBooks() {
        var actualBooks = repository.findAll();
        var expectedBooks = getDbBooks();
        assertThat(actualBooks).isNotEmpty()
            .hasSameSizeAs(expectedBooks);
        assertThat(actualBooks)
            .containsExactlyInAnyOrderElementsOf(expectedBooks);
    }

    @Test
    void shouldInsertNewBook() {
        var newBook = new Book();
        newBook.setTitle("BookTitle_4");
        var author = getFirstDbAuthor();
        newBook.setAuthor(author);
        var genres = getTwoFirstDbGenres();
        for (Genre genre : genres) {
            newBook.addGenre(genre);
        }
        Book lastBook = getDbBooks().get(getDbBooks().size() - 1);
        long nextBookId = lastBook.getId() + 1;
        assertThat(em.find(Book.class, nextBookId)).isNull();
        var savedBook = repository.save(newBook);
        assertThat(savedBook.getId()).isEqualTo(nextBookId);
        savedBook = em.find(Book.class, nextBookId);
        assertThat(savedBook.getAuthor().getId())
            .isEqualTo(author.getId());    
        assertThat(savedBook.getGenres()).isNotEmpty()
            .hasSameSizeAs(genres);
        assertThat(savedBook.getGenres())
            .containsExactlyInAnyOrderElementsOf(genres);
    }

    @Test
    void shouldUpdateBook() {
        Book lastBook = getDbBooks().get(getDbBooks().size() - 1);
        long bookToUpdateId = lastBook.getId();        
        var book = em.find(Book.class, bookToUpdateId);
        var newTitle = "BookTitle_3.2";
        book.setTitle(newTitle);
        var author = getFirstDbAuthor();
        book.setAuthor(author);
        var genres = getTwoFirstDbGenres();
        book.removeGenres();
        for (Genre genre : genres) {
            book.addGenre(genre);
        }
        repository.save(book);
        book = em.find(Book.class, bookToUpdateId);
        assertThat(book.getTitle()).isEqualTo(newTitle);
        assertThat(book.getAuthor().getId())
            .isEqualTo(author.getId());
        assertThat(book.getGenres()).isNotEmpty()
            .hasSameSizeAs(genres);
        assertThat(book.getGenres())
            .containsExactlyInAnyOrderElementsOf(genres);           
    }

    @Test
    void shouldDeleteBook() {
        Book lastBook = getDbBooks().get(getDbBooks().size() - 1);
        long bookToDeleteId = lastBook.getId();              
        repository.deleteById(bookToDeleteId);
        assertThat(em.find(Book.class, bookToDeleteId)).isNull();
    }

    private static List<Book> getDbBooks() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Book(Long.valueOf(id), "BookTitle_" + id, null, null))
                .toList();
    }

    private static Author getFirstDbAuthor() {
        return new Author(1L, "Author_" + 1);
    }

    private static List<Genre> getTwoFirstDbGenres() {
        return List.of(
            new Genre(1L, "Genre_" + 1),
            new Genre(2L, "Genre_" + 2)
        );
    }
}
