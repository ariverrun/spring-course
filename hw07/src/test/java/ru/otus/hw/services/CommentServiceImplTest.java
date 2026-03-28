package ru.otus.hw.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

@DataJpaTest
@Transactional(propagation = Propagation.NEVER)
@Import(CommentServiceImpl.class)
public class CommentServiceImplTest {

    @Autowired
    private CommentServiceImpl commentService;
    
    @ParameterizedTest
    @MethodSource("getDbBookIdsWithComments")
    void shouldFindCommentsByBookId(Long bookId, List<Comment> expectedComments) {
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
    @Transactional
    @Rollback
    void shouldInsertNewComment(Comment expectedComment) {
        var savedComment = commentService.insert(
            expectedComment.getText(), 
            expectedComment.getBook().getId()
        );
        assertThat(savedComment)
            .usingRecursiveComparison()
            .isEqualTo(expectedComment);
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, 1000L})
    @Transactional
    @Rollback
    void shouldThrowExceptionOnInsertWhenBookNotFound(Long nonExistentBookId) {
        assertThatThrownBy(() -> commentService.insert("Comment_100", nonExistentBookId))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Book with id %d not found".formatted(nonExistentBookId));
    }

    @ParameterizedTest
    @MethodSource("getCommentsForUpdate")
    @Transactional
    @Rollback
    void shouldUpdateComment(Comment expectedComment) {
        var savedComment = commentService.update(
            expectedComment.getId(),
            expectedComment.getText(), 
            expectedComment.getBook().getId()
        );
        assertThat(savedComment)
            .usingRecursiveComparison()
            .isEqualTo(expectedComment);
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, 1000L})
    @Transactional
    @Rollback
    void shouldThrowExceptionOnUpdateWhenBookNotFound(Long nonExistentBookId) {
        assertThatThrownBy(() -> commentService.update(1L, "Comment_100", nonExistentBookId))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Book with id %d not found".formatted(nonExistentBookId));
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    @Transactional
    @Rollback
    void shouldDeleteComment(Long commentId) {
        assertThat(commentService.findById(commentId).isPresent());
        commentService.deleteById(commentId);
        assertThat(commentService.findById(commentId).isEmpty());
    }
    
    private static Stream<Arguments> getDbBookIdsWithComments() {
        return Stream.of(
            Arguments.of(
                1L,
                List.of(
                    new Comment(1L, new Book(1L, "BookTitle_1", null, null), "Comment_1"),
                    new Comment(2L, new Book(1L, "BookTitle_1", null, null), "Comment_2")
                )
            ),
            Arguments.of(
                2L,
                List.of(
                    new Comment(3L, new Book(2L, "BookTitle_2", null, null), "Comment_3")
                )
            ),
            Arguments.of(
                3L,
                List.of(
                    new Comment(4L, new Book(3L, "BookTitle_3", null, null), "Comment_4")
                )
            )
        );
    }
    
    private static List<Comment> getDbComments() {
        return IntStream.range(1, 5).boxed()
                .map(id -> new Comment(id, new Book(), "Comment_" + id))
                .toList();
    }

    private static List<Comment> getCommentsForInsert() {
        Author author1 = new Author(1L, "Author_1");
        Author author2 = new Author(2L, "Author_2");
        
        Genre genre1 = new Genre(1L, "Genre_1");
        Genre genre2 = new Genre(2L, "Genre_2");
        Genre genre3 = new Genre(3L, "Genre_3");
        Genre genre4 = new Genre(4L, "Genre_4");
        
        Book book1 = new Book(1L, "BookTitle_1", author1, new ArrayList<>());
        book1.addGenre(genre1);
        book1.addGenre(genre2);
        
        Book book2 = new Book(2L, "BookTitle_2", author2, new ArrayList<>());
        book2.addGenre(genre3);
        book2.addGenre(genre4);

        Comment comment5 = new Comment(5L, book1, "Comment_5");
        Comment comment6 = new Comment(6L, book2, "Comment_6");

        return List.of(comment5, comment6);
    }

    private static List<Comment> getCommentsForUpdate() {
        Author author2 = new Author(2L, "Author_2");
        Author author3 = new Author(3L, "Author_3");
        
        Genre genre3 = new Genre(3L, "Genre_3");
        Genre genre4 = new Genre(4L, "Genre_4");
        Genre genre5 = new Genre(5L, "Genre_5");
        Genre genre6 = new Genre(6L, "Genre_6");
        
        Book book2 = new Book(2L, "BookTitle_2", author2, new ArrayList<>());
        book2.addGenre(genre3);
        book2.addGenre(genre4);
        Comment comment1 = new Comment(1L, book2, "Comment_1.1");
        
        Book book3 = new Book(3L, "BookTitle_3", author3, new ArrayList<>());
        book3.addGenre(genre5);
        book3.addGenre(genre6);
        Comment comment3 = new Comment(3L, book3, "Comment_3.1");
        
        return List.of(comment1, comment3);
    }
}
