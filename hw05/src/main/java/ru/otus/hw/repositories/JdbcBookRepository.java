package ru.otus.hw.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final NamedParameterJdbcOperations jdbc;

    private final GenreRepository genreRepository;

    @Override
    public Optional<Book> findById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        var book = jdbc.query(
            """        
            SELECT b.id, b.title, b.author_id, a.full_name 
            FROM books b 
            LEFT JOIN authors a 
            ON b.author_id = a.id 
            WHERE b.id = :id        
            """, 
            params, 
            new BookResultSetExtractor()
        );
        if (book != null) {
            var relations = getBookGenreRelations(book);
            var genres = genreRepository.findAllByIds(
                relations.stream()
                    .map(BookGenreRelation::genreId)
                    .collect(Collectors.toSet())
            );
            mergeBooksInfo(book, genres, relations);
        }
        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var books = getAllBooksWithoutGenres();
        var relations = getAllGenreRelations();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        jdbc.update(
            "DELETE FROM authors where id = :id", 
            Collections.singletonMap("id", id)
        );
    }

    private List<Book> getAllBooksWithoutGenres() {
        return jdbc.query(
            "SELECT b.id, b.title, b.author_id, a.full_name FROM books b LEFT JOIN authors a ON b.author_id = a.id", 
            new BookRowMapper()
        );
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return jdbc.query("SELECT book_id, genre_id FROM books_genres", new BookGenreRelationRowMapper());
    }

    private List<BookGenreRelation> getBookGenreRelations(Book book) {
        return jdbc.query(
            "SELECT book_id, genre_id FROM books_genres WHERE book_id = :book_id",
            Collections.singletonMap("book_id", book.getId()),
            new BookGenreRelationRowMapper()
        );
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        Map<Long, Book> bookMap = booksWithoutGenres.stream()
            .collect(Collectors.toMap(Book::getId, Function.identity()));
        
        Map<Long, Genre> genreMap = genres.stream()
            .collect(Collectors.toMap(Genre::getId, Function.identity()));
            
        for (BookGenreRelation relation : relations) {
            if (bookMap.containsKey(relation.bookId()) && genreMap.containsKey(relation.genreId())) {
                Book book = bookMap.get(relation.bookId());
                book.addGenre(genreMap.get(relation.genreId()));
            }
        }
    }

    private void mergeBooksInfo(Book bookWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        Map<Long, Genre> genreMap = genres.stream()
            .collect(Collectors.toMap(Genre::getId, Function.identity()));
            
        for (BookGenreRelation relation : relations) {
            if (relation.bookId() == bookWithoutGenres.getId() && genreMap.containsKey(relation.genreId())) {
                bookWithoutGenres.addGenre(genreMap.get(relation.genreId()));
            }
        }
    }

    private Book insert(Book book) {
        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("title", book.getTitle())
            .addValue("author_id", book.getAuthor().getId());
        var keyHolder = new GeneratedKeyHolder();

        jdbc.update(
            "INSERT INTO books (title, author_id) values (:title, :author_id)", 
            params, 
            keyHolder, 
            new String[] {"id"}
        );

        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("id", book.getId())
            .addValue("title", book.getTitle())
            .addValue("author_id", book.getAuthor().getId());
        
        int updatedCount = jdbc.update(
            "UPDATE books SET title = :title, author_id = :author_id WHERE id = :id",
            params
        );
        
        if (updatedCount == 0) {
            throw new EntityNotFoundException("Book with id %d not found".formatted(book.getId()));
        }

        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        SqlParameterSource[] batchArgs = book.getGenres().stream()
            .map(genre -> new MapSqlParameterSource()
                .addValue("book_id", book.getId())
                .addValue("genre_id", genre.getId()))
            .toArray(SqlParameterSource[]::new);
        
        jdbc.batchUpdate(
            "INSERT INTO books_genres (book_id, genre_id) VALUES (:book_id, :genre_id)",
            batchArgs
        );
    }

    private void removeGenresRelationsFor(Book book) {
        Map<String, Object> params = Collections.singletonMap("book_id", book.getId());
        jdbc.update("DELETE FROM books_genres WHERE book_id = :book_id", params);
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Book(
                rs.getLong("id"),
                rs.getString("title"),
                new Author(
                    rs.getLong("author_id"),
                    rs.getString("full_name")
                ),
                new ArrayList<>()
            );            
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs.next()) {
                return new Book(
                    rs.getLong("id"),
                    rs.getString("title"),
                    new Author(
                        rs.getLong("author_id"),
                        rs.getString("full_name")
                    ),
                    new ArrayList<>()
                );
            }
            return null;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }

    private static class BookGenreRelationRowMapper implements RowMapper<BookGenreRelation> {

        @Override
        public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new BookGenreRelation(
                rs.getLong("book_id"),
                rs.getLong("genre_id")
            );
        }
    }    
}
