package ru.otus.hw.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;

import ru.otus.hw.fixtures.FixturesLoader;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.CommentRepository;

@DataMongoTest
@Import({BookServiceImpl.class, FixturesLoader.class})
public class BookServiceImplTest {

    @Autowired
    private BookServiceImpl bookService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FixturesLoader fixturesLoader;

    @BeforeEach
    void setUp() {
        fixturesLoader.purge();
        fixturesLoader.load();
    }

    @Test
    void shouldFindAllBooks() {
        List<Book> books = bookService.findAll();
        List<Book> expectedBooks = getDbBooks();
        assertThat(books)
            .usingRecursiveComparison()
            .isEqualTo(expectedBooks);
    }

    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldFindBookById(Book expectedBook) {
        var optionalBook = bookService.findById(expectedBook.getId());
        assertThat(optionalBook).isPresent();
        assertThat(optionalBook.get())
            .usingRecursiveComparison()
            .isEqualTo(expectedBook);
    }

    @ParameterizedTest
    @MethodSource("getBooksForInsert")
    void shouldInsertNewBook(Book expectedBook) {   
        var savedBook = bookService.insert(
            expectedBook.getTitle(), 
            expectedBook.getAuthor().getId(), 
            expectedBook.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet())
        );
        assertThat(savedBook)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(expectedBook);
    }

    @ParameterizedTest
    @MethodSource("getBooksForUpdate")
    void shouldUpdateBook(Book expectedBook) {
        var savedBook = bookService.update(
            expectedBook.getId(),
            expectedBook.getTitle(), 
            expectedBook.getAuthor().getId(), 
            expectedBook.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet())
        );
        assertThat(savedBook)
            .usingRecursiveComparison()
            .isEqualTo(expectedBook);
    }

    @ParameterizedTest
    @MethodSource("getBookIdsForDelete")
    void shouldDeleteBook(String bookId) {
        assertThat(commentRepository.findByBookId(bookId)).isNotEmpty();
        bookService.deleteById(bookId);
        assertThat(bookService.findById(bookId)).isNotPresent();
        assertThat(commentRepository.findByBookId(bookId)).isEmpty();
    }

    private static List<Book> getDbBooks() {
        Author author1 = new Author("1", "Author_1");
        Author author2 = new Author("2", "Author_2");
        Author author3 = new Author("3", "Author_3");
        
        Genre genre1 = new Genre("1", "Genre_1");
        Genre genre2 = new Genre("2", "Genre_2");
        Genre genre3 = new Genre("3", "Genre_3");
        Genre genre4 = new Genre("4", "Genre_4");
        Genre genre5 = new Genre("5", "Genre_5");
        Genre genre6 = new Genre("6", "Genre_6");
        
        Book book1 = new Book("1", "BookTitle_1", author1, new ArrayList<>());
        book1.addGenre(genre1);
        book1.addGenre(genre2);
        
        Book book2 = new Book("2", "BookTitle_2", author2, new ArrayList<>());
        book2.addGenre(genre3);
        book2.addGenre(genre4);
        
        Book book3 = new Book("3", "BookTitle_3", author3, new ArrayList<>());
        book3.addGenre(genre5);
        book3.addGenre(genre6);
        
        return List.of(book1, book2, book3);
    }

    private static List<Book> getBooksForInsert() {
        Author author1 = new Author("1", "Author_1");
        Author author2 = new Author("2", "Author_2");

        Genre genre1 = new Genre("1", "Genre_1");
        Genre genre2 = new Genre("2", "Genre_2");

        Book book4 = new Book("4", "BookTitle_4", author1, new ArrayList<>());
        book4.addGenre(genre1);
        book4.addGenre(genre2);
        
        Book book5 = new Book("5", "BookTitle_5", author2, new ArrayList<>());
        book5.addGenre(genre1);

        return List.of(book4, book5);
    }

    private static List<Book> getBooksForUpdate() {
        Author author2 = new Author("2", "Author_2");
        Author author3 = new Author("3", "Author_3");
        
        Genre genre1 = new Genre("1", "Genre_1");
        Genre genre3 = new Genre("3", "Genre_3");
        Genre genre4 = new Genre("4", "Genre_4");
        
        Book book1 = new Book("1", "BookTitle_1.1", author2, new ArrayList<>());
        book1.addGenre(genre3);
        book1.addGenre(genre4);
        
        Book book2 = new Book("2", "BookTitle_2.1", author3, new ArrayList<>());
        book2.addGenre(genre1);
        book2.addGenre(genre4);
        
        return List.of(book1, book2);
    }

    private static List<String> getBookIdsForDelete() {
        return List.of("1", "3");
    }
}
