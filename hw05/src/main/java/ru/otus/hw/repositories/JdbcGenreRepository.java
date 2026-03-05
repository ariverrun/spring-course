package ru.otus.hw.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.models.Genre;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {

    private final NamedParameterJdbcOperations jdbc;

    @Override
    public List<Genre> findAll() {
        return jdbc.query("SELECT id, name FROM genres", new GenreRowMapper());
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        return new ArrayList<>();
    }

    private static class GenreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int i) throws SQLException {
            return new Genre(
                rs.getLong("id"),
                rs.getString("name")
            );
        }
    }
}
