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
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BooksRepositoryCustomImpl;

@DataMongoTest
@Import({GenreServiceImpl.class, BooksRepositoryCustomImpl.class, FixturesLoader.class})
public class GenreServiceImplTest {
    
    @Autowired
    private GenreServiceImpl genreService;

    @Autowired
    private FixturesLoader fixturesLoader;

    @BeforeEach
    void setUp() {
        fixturesLoader.purge();
        fixturesLoader.load();
    }
    
    @Test
    void shouldFindAllGenres() {
        var genres = genreService.findAll();
        var expectedGenres = getDbGenres();
        assertThat(genres)
            .usingRecursiveComparison()
            .isEqualTo(expectedGenres);
    }

    @ParameterizedTest
    @MethodSource("getDbGenres")
    void shouldFindGenreById(Genre expectedGenre) {
        var optionalGenre = genreService.findById(expectedGenre.getId());
        assertThat(optionalGenre).isPresent();
        assertThat(optionalGenre.get())
            .usingRecursiveComparison()
            .isEqualTo(expectedGenre);
    }

    @ParameterizedTest
    @MethodSource("getGenresForInsert")
    void shouldInsertNewBook(Genre expectedGenre) {
        var savedGenre = genreService.insert(
            expectedGenre.getName()
        );
        assertThat(savedGenre)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(expectedGenre);
    }

    @ParameterizedTest
    @MethodSource("getGenresForUpdate")
    void shouldUpdateAuthor(Genre expectedGenre) {
        var savedGenre = genreService.update(
            expectedGenre.getId(),
            expectedGenre.getName()
        );
        assertThat(savedGenre)
            .usingRecursiveComparison()
            .isEqualTo(expectedGenre);
    }

    @ParameterizedTest
    @MethodSource("getGenreIdsForDelete")
    void shouldDeleteAuthor(String genreId) {
        assertThat(genreService.findById(genreId)).isNotEmpty();
        genreService.deleteById(genreId);
        assertThat(genreService.findById(genreId)).isNotPresent();
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(id.toString(), "Genre_" + id))
                .toList();
    }

    private static List<Genre> getGenresForInsert() {
        var genre7 = new Genre();
        genre7.setName("Genre_7");
        var genre8 = new Genre();
        genre8.setName("Genre_8");
        return List.of(genre7, genre8);
    }
    
    private static List<Genre> getGenresForUpdate() {
        var genre2 = new Genre("2", "Genre_2.1");
        var genre3 = new Genre("3", "Genre_3.1");
        return List.of(genre2, genre3);
    }
    
    private static List<String> getGenreIdsForDelete() {
        return List.of("1", "3");
    }    
}
