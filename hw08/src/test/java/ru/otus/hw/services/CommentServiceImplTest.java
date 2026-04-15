package ru.otus.hw.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;

import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.fixtures.FixturesLoader;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

@DataMongoTest
@Import({CommentServiceImpl.class, FixturesLoader.class})
public class CommentServiceImplTest {

    @Autowired
    private CommentServiceImpl commentService;
    
    @Autowired
    private FixturesLoader fixturesLoader;

    @BeforeEach
    void setUp() {
        fixturesLoader.purge();
        fixturesLoader.load();
    }

    @ParameterizedTest
    @MethodSource("getDbBookIdsWithComments")
    void shouldFindCommentsByBookId(String bookId, List<Comment> expectedComments) {
        List<Comment> comments = commentService.findByBookId(bookId);
        assertThat(comments)
            .usingRecursiveComparison()
            .ignoringFields("book")
            .isEqualTo(expectedComments);
    }

    @ParameterizedTest
    @MethodSource("getDbComments")
    void shouldFindCommentById(Comment expectedComment) {
        var optionalComment = commentService.findById(expectedComment.getId());
        assertThat(optionalComment).isPresent();
        assertThat(optionalComment.get())
            .usingRecursiveComparison()
            .ignoringFields("book")
            .isEqualTo(expectedComment);
    }

    @ParameterizedTest
    @MethodSource("getCommentsForInsert")
    void shouldInsertNewComment(Comment expectedComment) {
        var savedComment = commentService.insert(
            expectedComment.getText(), 
            expectedComment.getBook().getId()
        );
        assertThat(savedComment)
            .usingRecursiveComparison()
            .ignoringFields("id", "book")
            .isEqualTo(expectedComment);
    }

    @ParameterizedTest
    @ValueSource(strings = {"999", "1000"})
    void shouldThrowExceptionOnInsertWhenBookNotFound(String nonExistentBookId) {
        assertThatThrownBy(() -> commentService.insert("Comment_100", nonExistentBookId))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Book with id %s not found".formatted(nonExistentBookId));
    }

    @ParameterizedTest
    @MethodSource("getCommentsForUpdate")
    void shouldUpdateComment(Comment expectedComment) {
        var savedComment = commentService.update(
            expectedComment.getId(),
            expectedComment.getText(), 
            expectedComment.getBook().getId()
        );
        assertThat(savedComment)
            .usingRecursiveComparison()
            .ignoringFields("book")
            .isEqualTo(expectedComment);
    }

    @ParameterizedTest
    @ValueSource(strings = {"999", "1000"})
    void shouldThrowExceptionOnUpdateWhenBookNotFound(String nonExistentBookId) {
        assertThatThrownBy(() -> commentService.update("1", "Comment_100", nonExistentBookId))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Book with id %s not found".formatted(nonExistentBookId));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2"})
    void shouldDeleteComment(String commentId) {
        assertThat(commentService.findById(commentId).isPresent());
        commentService.deleteById(commentId);
        assertThat(commentService.findById(commentId).isEmpty());
    }
    
    private static Stream<Arguments> getDbBookIdsWithComments() {
        return Stream.of(
            Arguments.of(
                "1",
                List.of(
                    new Comment("1", new Book("1", "BookTitle_1", null, null), "Comment_1"),
                    new Comment("2", new Book("1", "BookTitle_1", null, null), "Comment_2")
                )
            ),
            Arguments.of(
                "2",
                List.of(
                    new Comment("3", new Book("2", "BookTitle_2", null, null), "Comment_3")
                )
            ),
            Arguments.of(
                "3",
                List.of(
                    new Comment("4", new Book("3", "BookTitle_3", null, null), "Comment_4")
                )
            )
        );
    }
    
    private static List<Comment> getDbComments() {
        return IntStream.range(1, 5).boxed()
                .map(id -> new Comment(id.toString(), new Book(), "Comment_" + id))
                .toList();
    }

    private static List<Comment> getCommentsForInsert() {
        Author author1 = new Author("1", "Author_1");
        Author author2 = new Author("2", "Author_2");
        
        Genre genre1 = new Genre("2", "Genre_1");
        Genre genre2 = new Genre("2", "Genre_2");
        Genre genre3 = new Genre("3", "Genre_3");
        Genre genre4 = new Genre("4", "Genre_4");
        
        Book book1 = new Book("1", "BookTitle_1", author1, new ArrayList<>());
        book1.addGenre(genre1);
        book1.addGenre(genre2);
        
        Book book2 = new Book("2", "BookTitle_2", author2, new ArrayList<>());
        book2.addGenre(genre3);
        book2.addGenre(genre4);

        Comment comment5 = new Comment("5", book1, "Comment_5");
        Comment comment6 = new Comment("6", book2, "Comment_6");

        return List.of(comment5, comment6);
    }

    private static List<Comment> getCommentsForUpdate() {
        Author author2 = new Author("2", "Author_2");
        Author author3 = new Author("3", "Author_3");
        
        Genre genre3 = new Genre("3", "Genre_3");
        Genre genre4 = new Genre("4", "Genre_4");
        Genre genre5 = new Genre("5", "Genre_5");
        Genre genre6 = new Genre("6", "Genre_6");
        
        Book book2 = new Book("2", "BookTitle_2", author2, new ArrayList<>());
        book2.addGenre(genre3);
        book2.addGenre(genre4);
        Comment comment1 = new Comment("1", book2, "Comment_1.1");
        
        Book book3 = new Book("3", "BookTitle_3", author3, new ArrayList<>());
        book3.addGenre(genre5);
        book3.addGenre(genre6);
        Comment comment3 = new Comment("3", book3, "Comment_3.1");
        
        return List.of(comment1, comment3);
    }
}
