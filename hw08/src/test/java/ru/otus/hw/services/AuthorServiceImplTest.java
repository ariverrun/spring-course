package ru.otus.hw.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;

import ru.otus.hw.fixtures.FixturesLoader;
import ru.otus.hw.models.Author;

@DataMongoTest
@Import({AuthorServiceImpl.class, FixturesLoader.class})
class AuthorServiceImplTest {

    @Autowired
    private AuthorServiceImpl authorService;

    @Autowired
    private FixturesLoader fixturesLoader;

    @BeforeEach
    void setUp() {
        fixturesLoader.purge();
        fixturesLoader.load();
    }

    @Test
    void shouldFindAllAuthors() {
        var authors = authorService.findAll();
        var expectedAuthors = getDbAuthors();
        assertThat(authors)
            .usingRecursiveComparison()
            .isEqualTo(expectedAuthors);
    }

    @ParameterizedTest
    @MethodSource("getDbAuthors")
    void shouldFindAuthorById(Author expectedAuthor) {
        var optionalAuthor = authorService.findById(expectedAuthor.getId());
        assertThat(optionalAuthor).isPresent();
        assertThat(optionalAuthor.get())
            .usingRecursiveComparison()
            .isEqualTo(expectedAuthor);
    }

    @ParameterizedTest
    @MethodSource("getAuthorsForInsert")
    void shouldInsertNewBook(Author expectedAuthor) {
        var savedAuthor = authorService.insert(
            expectedAuthor.getFullName()
        );
        assertThat(savedAuthor)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(expectedAuthor);
    }

    @ParameterizedTest
    @MethodSource("getAuthorsForUpdate")
    void shouldUpdateAuthor(Author expectedAuthor) {
        var savedAuthor = authorService.update(
            expectedAuthor.getId(),
            expectedAuthor.getFullName()
        );
        assertThat(savedAuthor)
            .usingRecursiveComparison()
            .isEqualTo(expectedAuthor);
    }

    @ParameterizedTest
    @MethodSource("getAuthorIdsForDelete")
    void shouldDeleteAuthor(String authorId) {
        assertThat(authorService.findById(authorId)).isNotEmpty();
        authorService.deleteById(authorId);
        assertThat(authorService.findById(authorId)).isNotPresent();
    }    

    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(id.toString(), "Author_" + id))
                .toList();
    }

    private static List<Author> getAuthorsForInsert() {
        Author author4 = new Author();
        author4.setFullName("Author_4");
        Author author5 = new Author();
        author5.setFullName("Author_5");
        return List.of(author4, author5);
    }
    
    private static List<Author> getAuthorsForUpdate() {
        Author author2 = new Author("2", "Author_2.1");
        Author author3 = new Author("3", "Author_3.1");
        return List.of(author2, author3);
    }

    private static List<String> getAuthorIdsForDelete() {
        return List.of("1", "3");
    }
}