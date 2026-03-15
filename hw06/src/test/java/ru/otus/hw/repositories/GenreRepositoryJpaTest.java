package ru.otus.hw.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import ru.otus.hw.models.Genre;

@DataJpaTest
@Import(GenreRepositoryJpa.class)
public class GenreRepositoryJpaTest {
    private static final long FIRST_GENRE_ID = 1L;
    private static final long SECOND_GENRE_ID = 2L;
    private static final long THIRD_GENRE_ID = 3L;
    private static final long FOURTH_GENRE_ID = 4L;
    private static final long FIFTH_GENRE_ID = 5L;
    private static final long SIXTH_GENRE_ID = 6L;
    private static final long SEVENTH_GENRE_ID = 7L;

    @Autowired
    private GenreRepositoryJpa repositoryJpa;

    @Autowired
    private TestEntityManager em;    

    @Test
    void shouldFindExpectedGenreById() {
        var optionalActualGenre = repositoryJpa.findById(FIRST_GENRE_ID);
        var expectedGenre = em.find(Genre.class, FIRST_GENRE_ID);
        assertThat(optionalActualGenre).isPresent().get()
            .usingRecursiveComparison()
            .isEqualTo(expectedGenre);
    }

    @Test
    void shouldFindAllGenres() {
        var actualGenres = repositoryJpa.findAll();
        var expectedGenres = List.of(
            em.find(Genre.class, FIRST_GENRE_ID),
            em.find(Genre.class, SECOND_GENRE_ID),
            em.find(Genre.class, THIRD_GENRE_ID),
            em.find(Genre.class, FOURTH_GENRE_ID),
            em.find(Genre.class, FIFTH_GENRE_ID),
            em.find(Genre.class, SIXTH_GENRE_ID)
        );

        assertThat(actualGenres).isNotEmpty()
            .hasSameSizeAs(expectedGenres);

        assertThat(actualGenres)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrderElementsOf(expectedGenres);
    }

    @Test
    void shouldInsertNewGenre() {
        var newGenre = new Genre();
        newGenre.setName("Genre_7");

        assertThat(em.find(Genre.class, SEVENTH_GENRE_ID)).isNull();

        var savedGenre = repositoryJpa.save(newGenre);
        assertThat(savedGenre.getId()).isEqualTo(SEVENTH_GENRE_ID);

        savedGenre = em.find(Genre.class, SEVENTH_GENRE_ID);

        assertThat(savedGenre)
            .usingRecursiveComparison()
            .isEqualTo(newGenre);        
    }

    @Test
    void shouldUpdateGenre() {
        var genre = em.find(Genre.class, SIXTH_GENRE_ID);

        var newName = "Genre_6.2";

        genre.setName(newName);

        repositoryJpa.save(genre);

        genre = em.find(Genre.class, SIXTH_GENRE_ID);

        assertThat(genre.getName()).isEqualTo(newName);
    }

    @Test
    void shouldDeleteGenre() {
        repositoryJpa.deleteById(SIXTH_GENRE_ID);

        assertThat(em.find(Genre.class, SIXTH_GENRE_ID)).isNull();
    }    
}
