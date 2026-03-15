package ru.otus.hw.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import ru.otus.hw.models.Author;

@DataJpaTest
@Import(AuthorRepositoryJpa.class)
public class AuthorRepositoryJpaTest {
 
    private static final long FIRST_AUTHOR_ID = 1L;
    private static final long SECOND_AUTHOR_ID = 2L;
    private static final long THIRD_AUTHOR_ID = 3L;
    private static final long FOURTH_AUTHOR_ID = 4L;

    @Autowired
    private AuthorRepositoryJpa repositoryJpa;

    @Autowired
    private TestEntityManager em;

    @Test
    void shouldFindExpectedAuthorById() {
        var optionalActualAuthor = repositoryJpa.findById(FIRST_AUTHOR_ID);
        var expectedAuthor = em.find(Author.class, FIRST_AUTHOR_ID);
        assertThat(optionalActualAuthor).isPresent().get()
            .usingRecursiveComparison()
            .isEqualTo(expectedAuthor);
    }

    @Test
    void shouldFindAllAuthors() {
        var actualAuthors = repositoryJpa.findAll();
        var expectedAuthors = List.of(
            em.find(Author.class, FIRST_AUTHOR_ID),
            em.find(Author.class, SECOND_AUTHOR_ID),
            em.find(Author.class, THIRD_AUTHOR_ID)
        );

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

        assertThat(em.find(Author.class, FOURTH_AUTHOR_ID)).isNull();

        var savedAuthor = repositoryJpa.save(newAuthor);
        assertThat(savedAuthor.getId()).isEqualTo(FOURTH_AUTHOR_ID);

        savedAuthor = em.find(Author.class, FOURTH_AUTHOR_ID);

        assertThat(savedAuthor)
            .usingRecursiveComparison()
            .isEqualTo(newAuthor);        
    }

    @Test
    void shouldUpdateAuthor() {
        var author = em.find(Author.class, THIRD_AUTHOR_ID);

        var newFullName = "Author_3.2";

        author.setFullName(newFullName);

        repositoryJpa.save(author);

        author = em.find(Author.class, THIRD_AUTHOR_ID);

        assertThat(author.getFullName()).isEqualTo(newFullName);
    }

    @Test
    void shouldDeleteAuthor() {
        repositoryJpa.deleteById(THIRD_AUTHOR_ID);

        assertThat(em.find(Author.class, THIRD_AUTHOR_ID)).isNull();
    }
}
