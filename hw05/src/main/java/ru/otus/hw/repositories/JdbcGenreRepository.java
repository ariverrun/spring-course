package ru.otus.hw.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.exceptions.EntityNotFoundException;
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
    public Optional<Genre> findById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        List<Genre> genres = jdbc.query(
            "SELECT id, name FROM genres WHERE id = :id", 
            params, 
            new GenreRowMapper()
        );
        return genres.stream().findFirst();
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

    @Override
    public Genre save(Genre genre) {
        if (genre.getId() == 0) {
            return insert(genre);
        }
        return update(genre);
    }    

    @Override
    public void deleteById(long id) {
        jdbc.update(
            "DELETE FROM genres where id = :id", 
            Collections.singletonMap("id", id)
        );    
    }

    private Genre insert(Genre genre) {
        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("name", genre.getName());
        var keyHolder = new GeneratedKeyHolder();

        jdbc.update(
            "INSERT INTO genres (name) values (:name)", 
            params, 
            keyHolder, 
            new String[] {"id"}
        );
        //noinspection DataFlowIssue
        genre.setId(keyHolder.getKeyAs(Long.class));
        return genre;
    }

    private Genre update(Genre genre) {
        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("id", genre.getId())
            .addValue("name", genre.getName());
        
        int updatedCount = jdbc.update(
            "UPDATE genres SET name = :name WHERE id = :id", 
            params
        );
        
        if (updatedCount == 0) {
            throw new EntityNotFoundException("Genre with id %d not found".formatted(genre.getId()));
        }
        
        return genre;
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
