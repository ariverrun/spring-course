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

import ru.otus.hw.models.Genre;

@DataJpaTest
public class GenreRepositoryTest {

    @Autowired
    private GenreRepository repository;

    @Autowired
    private TestEntityManager em;    

    @ParameterizedTest
    @MethodSource("getDbGenres")
    void shouldFindExpectedGenreById(Genre expectedGenre) {
        var optionalActualGenre = repository.findById(expectedGenre.getId());
        assertThat(optionalActualGenre).isPresent().get()
            .usingRecursiveComparison()
            .isEqualTo(expectedGenre);
    }

    @Test
    void shouldFindAllGenres() {
        var actualGenres = repository.findAll();
        var expectedGenres = getDbGenres();
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
        Genre lastGenre = getDbGenres().get(getDbGenres().size() - 1);
        long nextGenreId = lastGenre.getId() + 1;
        assertThat(em.find(Genre.class, nextGenreId)).isNull();
        var savedGenre = repository.save(newGenre);
        assertThat(savedGenre.getId()).isEqualTo(nextGenreId);
        savedGenre = em.find(Genre.class, nextGenreId);
        assertThat(savedGenre)
            .usingRecursiveComparison()
            .isEqualTo(newGenre);        
    }

    @Test
    void shouldUpdateGenre() {
        Genre lastGenre = getDbGenres().get(getDbGenres().size() - 1);
        long genreToUpdateId = lastGenre.getId();
        var genre = em.find(Genre.class, genreToUpdateId);
        var newName = "Genre_6.2";
        genre.setName(newName);
        repository.save(genre);
        genre = em.find(Genre.class, genreToUpdateId);
        assertThat(genre.getName()).isEqualTo(newName);
    }

    @Test
    void shouldDeleteGenre() {
        Genre lastGenre = getDbGenres().get(getDbGenres().size() - 1);
        long genreToDeleteId = lastGenre.getId();        
        repository.deleteById(genreToDeleteId);
        assertThat(em.find(Genre.class, genreToDeleteId)).isNull();
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }       
}
