package ru.otus.hw.batch;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class MigrationIdCache {

    private final Map<Long, String> authorIds = new HashMap<>();

    private final Map<Long, String> genreIds = new HashMap<>();

    private final Map<Long, String> bookIds = new HashMap<>();

    public void putAuthorId(long jpaId, String mongoId) {
        authorIds.put(jpaId, mongoId);
    }

    public String getAuthorId(long jpaId) {
        return authorIds.get(jpaId);
    }

    public void putGenreId(long jpaId, String mongoId) {
        genreIds.put(jpaId, mongoId);
    }

    public String getGenreId(long jpaId) {
        return genreIds.get(jpaId);
    }

    public void putBookId(long jpaId, String mongoId) {
        bookIds.put(jpaId, mongoId);
    }

    public String getBookId(long jpaId) {
        return bookIds.get(jpaId);
    }

    public void clear() {
        authorIds.clear();
        genreIds.clear();
        bookIds.clear();
    }
}
