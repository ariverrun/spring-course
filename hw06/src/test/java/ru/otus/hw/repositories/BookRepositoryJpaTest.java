package ru.otus.hw.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

@DataJpaTest
@Import(BookRepositoryJpa.class)
public class BookRepositoryJpaTest {
    
    private static final long FIRST_BOOK_ID = 1L;
    private static final long SECOND_BOOK_ID = 2L;
    private static final long THIRD_BOOK_ID = 3L;
    private static final long FOURTH_BOOK_ID = 4L;
    private static final long FIFTH_BOOK_ID = 5L;
    private static final long FIRST_AUTHOR_ID = 1L;
    private static final long FIRST_GENRE_ID = 1L;
    private static final long SECOND_GENRE_ID = 2L;

    @Autowired
    private BookRepositoryJpa repositoryJpa;

    @Autowired
    private TestEntityManager em;

    @Test
    void shouldFindExpectedBookById() {
        var optionalActualBook = repositoryJpa.findById(FIRST_BOOK_ID);
        var expectedBook = em.find(Book.class, FIRST_BOOK_ID);
        assertThat(optionalActualBook).isPresent().get()
            .usingRecursiveComparison()
            .isEqualTo(expectedBook);
    }

    @Test
    void shouldFindAllBooks() {
        var actualBooks = repositoryJpa.findAll();
        var expectedBooks = List.of(
            em.find(Book.class, FIRST_BOOK_ID),
            em.find(Book.class, SECOND_BOOK_ID),
            em.find(Book.class, THIRD_BOOK_ID)
        );

        assertThat(actualBooks).isNotEmpty()
            .hasSameSizeAs(expectedBooks);

        assertThat(actualBooks)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrderElementsOf(expectedBooks);
    }

    @Test
    void shouldInsertNewBook() {
        var newBook = new Book();
        newBook.setTitle("BookTitle_4");

        var author = em.find(Author.class, FIRST_AUTHOR_ID);
        newBook.setAuthor(author);
        var genres = List.of(
            em.find(Genre.class, FIRST_GENRE_ID),
            em.find(Genre.class, SECOND_GENRE_ID)
        );

        for (Genre genre : genres) {
            newBook.addGenre(genre);
        }

        assertThat(em.find(Book.class, FOURTH_BOOK_ID)).isNull();

        var savedBook = repositoryJpa.save(newBook);
        assertThat(savedBook.getId()).isEqualTo(FOURTH_BOOK_ID);

        savedBook = em.find(Book.class, FOURTH_BOOK_ID);

        assertThat(savedBook)
            .usingRecursiveComparison()
            .isEqualTo(newBook);
        assertThat(savedBook.getAuthor())
            .usingRecursiveComparison()
            .isEqualTo(author);
        assertThat(savedBook.getGenres())
            .usingRecursiveComparison()
            .isEqualTo(genres);     
    }

    @Test
    void shouldUpdateBook() {
        var book = em.find(Book.class, THIRD_BOOK_ID);

        var newTitle = "BookTitle_3.2";
        book.setTitle(newTitle);

        var author = em.find(Author.class, FIRST_AUTHOR_ID);
        book.setAuthor(author);
        var genres = List.of(
            em.find(Genre.class, FIRST_GENRE_ID),
            em.find(Genre.class, SECOND_GENRE_ID)
        );
        book.removeGenres();
        for (Genre genre : genres) {
            book.addGenre(genre);
        }
        repositoryJpa.save(book);

        book = em.find(Book.class, THIRD_BOOK_ID);

        assertThat(book.getTitle()).isEqualTo(newTitle);
        assertThat(book.getAuthor())
            .usingRecursiveComparison()
            .isEqualTo(author);
        assertThat(book.getGenres())
            .usingRecursiveComparison()
            .isEqualTo(genres);           
    }

    @Test
    void shouldDeleteBook() {
        repositoryJpa.deleteById(THIRD_BOOK_ID);
        assertThat(em.find(Book.class, THIRD_BOOK_ID)).isNull();
    }

    @Test
    void shouldCheckIfBookExists() {
        assertThat(repositoryJpa.existsById(SECOND_BOOK_ID)).isTrue();
        assertThat(repositoryJpa.existsById(FIFTH_BOOK_ID)).isFalse();
    }       
}
