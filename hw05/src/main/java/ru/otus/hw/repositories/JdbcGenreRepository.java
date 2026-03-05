package ru.otus.hw.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
        Map<String, Object> params = Collections.singletonMap("ids", ids);
        List<Genre> genres = jdbc.query(
            "SELECT id, name FROM genres WHERE id IN (:ids)", 
            params, 
            new GenreRowMapper()
        );        

        return genres;
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
