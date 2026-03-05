package ru.otus.hw.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;

@Repository
@RequiredArgsConstructor
public class JdbcAuthorRepository implements AuthorRepository {

    private final NamedParameterJdbcOperations jdbc;

    @Override
    public List<Author> findAll() {
        return jdbc.query("SELECT id, full_name FROM authors", new AuthorRowMapper());
    }

    @Override
    public Optional<Author> findById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        List<Author> authors = jdbc.query(
            "SELECT id, full_name FROM authors WHERE id = :id", 
            params, 
            new AuthorRowMapper()
        );
        return authors.stream().findFirst();
    }

    @Override
    public Author save(Author author) {
        if (author.getId() == 0) {
            return insert(author);
        }
        return update(author);
    }

    @Override
    public void deleteById(long id) {
        jdbc.update(
            "DELETE FROM authors where id = :id", 
            Collections.singletonMap("id", id)
        );    
    }


    private Author insert(Author author) {
        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("full_name", author.getFullName());
        var keyHolder = new GeneratedKeyHolder();

        jdbc.update(
            "INSERT INTO authors (full_name) values (:full_name)", 
            params, 
            keyHolder, 
            new String[] {"id"}
        );
        //noinspection DataFlowIssue
        author.setId(keyHolder.getKeyAs(Long.class));
        return author;
    }

    private Author update(Author author) {
        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("id", author.getId())
            .addValue("full_name", author.getFullName());
        
        int updatedCount = jdbc.update(
            "UPDATE authors SET full_name = :full_name WHERE id = :id", 
            params
        );
        
        if (updatedCount == 0) {
            throw new EntityNotFoundException("Author with id %d not found".formatted(author.getId()));
        }
        
        return author;
    }

    private static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet rs, int i) throws SQLException {
            return new Author(
                rs.getLong("id"),
                rs.getString("full_name")
            );
        }
    }
}
