package ru.otus.hw.batch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MigrationIdCacheTest {

    private MigrationIdCache cache;

    @BeforeEach
    void setUp() {
        cache = new MigrationIdCache();
    }

    @Test
    void shouldReturnStoredMongoAuthorId() {
        cache.putAuthorId(1L, "mongo-author-1");
        assertThat(cache.getAuthorId(1L)).isEqualTo("mongo-author-1");
    }

    @Test
    void shouldReturnStoredMongoGenreId() {
        cache.putGenreId(2L, "mongo-genre-2");
        assertThat(cache.getGenreId(2L)).isEqualTo("mongo-genre-2");
    }

    @Test
    void shouldReturnStoredMongoBookId() {
        cache.putBookId(3L, "mongo-book-3");
        assertThat(cache.getBookId(3L)).isEqualTo("mongo-book-3");
    }

    @Test
    void shouldNotCollideAcrossEntityTypes() {
        cache.putAuthorId(1L, "mongo-author-1");
        cache.putGenreId(1L, "mongo-genre-1");
        cache.putBookId(1L, "mongo-book-1");

        assertThat(cache.getAuthorId(1L)).isEqualTo("mongo-author-1");
        assertThat(cache.getGenreId(1L)).isEqualTo("mongo-genre-1");
        assertThat(cache.getBookId(1L)).isEqualTo("mongo-book-1");
    }

    @Test
    void shouldReturnNullForUnknownId() {
        assertThat(cache.getAuthorId(999L)).isNull();
    }
}
