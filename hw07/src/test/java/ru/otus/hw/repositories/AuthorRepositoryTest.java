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

@DataJpaTest
public class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository repository;

    @Autowired
    private TestEntityManager em;

    @ParameterizedTest
    @MethodSource("getDbAuthors")
    void shouldFindExpectedAuthorById(Author expectedAuthor) {
        var optionalActualAuthor = repository.findById(expectedAuthor.getId());
        assertThat(optionalActualAuthor).isPresent().get()
            .usingRecursiveComparison()
            .isEqualTo(expectedAuthor);
    }

    @Test
    void shouldFindAllAuthors() {
        var actualAuthors = repository.findAll();
        var expectedAuthors = getDbAuthors();
        assertThat(actualAuthors).isNotEmpty()
            .hasSameSizeAs(expectedAuthors);
        assertThat(actualAuthors)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrderElementsOf(expectedAuthors);
    }

    @Test
    void shouldInsertNewAuthor() {
        var newAuthor = new Author();
        newAuthor.setFullName("Author_4");
        Author lastAuthor = getDbAuthors().get(getDbAuthors().size() - 1);
        long nextAuthorId = lastAuthor.getId() + 1;
        assertThat(em.find(Author.class, nextAuthorId)).isNull();
        var savedAuthor = repository.save(newAuthor);
        assertThat(savedAuthor.getId()).isEqualTo(nextAuthorId);
        savedAuthor = em.find(Author.class, nextAuthorId);
        assertThat(savedAuthor)
            .usingRecursiveComparison()
            .isEqualTo(newAuthor);        
    }

    @Test
    void shouldUpdateAuthor() {
        Author lastAuthor = getDbAuthors().get(getDbAuthors().size() - 1);
        long authorToUpdateId = lastAuthor.getId();
        var author = em.find(Author.class, authorToUpdateId);
        var newFullName = "Author_3.2";
        author.setFullName(newFullName);
        repository.save(author);
        author = em.find(Author.class, authorToUpdateId);
        assertThat(author.getFullName()).isEqualTo(newFullName);
    }

    @Test
    void shouldDeleteAuthor() {
        Author lastAuthor = getDbAuthors().get(getDbAuthors().size() - 1);
        long authorToDeleteId = lastAuthor.getId();        
        repository.deleteById(authorToDeleteId);
        assertThat(em.find(Author.class, authorToDeleteId)).isNull();
    }

    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(id, "Author_" + id))
                .toList();
    }    
}
