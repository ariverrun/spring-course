package ru.otus.hw.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

@DataJpaTest
@Import({CommentRepositoryJpa.class, BookRepositoryJpa.class})
public class CommentRepositoryJpaTest {

    @Autowired
    private CommentRepositoryJpa repositoryJpa;

    @Autowired
    private TestEntityManager em;

    @ParameterizedTest
    @MethodSource("getDbComments")
    void shouldFindExpectedCommentById(Comment expectedComment) {
        var optionalActualComment = repositoryJpa.findById(expectedComment.getId());
        assertThat(optionalActualComment).isPresent().get()
            .isEqualTo(expectedComment);
    }

    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldFindCommentsByBookId(Book book) {
        var actualComments = repositoryJpa.findByBookId(book.getId());
        var expectedComments = getDbCommentsByBookId(book.getId());
        assertThat(actualComments).isNotEmpty()
            .hasSameSizeAs(expectedComments);
        assertThat(actualComments)
            .usingRecursiveFieldByFieldElementComparatorIgnoringFields("book")
            .containsExactlyInAnyOrderElementsOf(expectedComments);
    }

    @Test
    void shouldInsertNewComment() {
        var newComment = new Comment();
        newComment.setText("Comment_5");
        Comment lastComment = getDbComments().get(getDbComments().size() - 1);
        long nextCommentId = lastComment.getId() + 1;
        assertThat(em.find(Comment.class, nextCommentId)).isNull();
        var savedComment = repositoryJpa.save(newComment);
        assertThat(savedComment.getId()).isEqualTo(nextCommentId);
        savedComment = em.find(Comment.class, nextCommentId);
        assertThat(savedComment)
            .usingRecursiveComparison()
            .isEqualTo(newComment);        
    }

    @Test
    void shouldUpdateComment() {
        Comment lastComment = getDbComments().get(getDbComments().size() - 1);
        long сommentToUpdateId = lastComment.getId();
        var comment = em.find(Comment.class, сommentToUpdateId);
        var newText = "Comment_3.2";
        comment.setText(newText);
        repositoryJpa.save(comment);
        comment = em.find(Comment.class, сommentToUpdateId);
        assertThat(comment.getText()).isEqualTo(newText);
    }

    @Test
    void shouldDeleteComment() {
        Comment lastComment = getDbComments().get(getDbComments().size() - 1);
        long сommentToDeleteId = lastComment.getId();        
        repositoryJpa.deleteById(сommentToDeleteId);
        assertThat(em.find(Comment.class, сommentToDeleteId)).isNull();
    }

    private static List<Comment> getDbComments() {
        return IntStream.range(1, 5).boxed()
                .map(id -> new Comment(id, new Book(), "Comment_" + id))
                .toList();
    }

    private static List<Book> getDbBooks() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Book(id, "BookTitle_" + id, null, null))
                .toList();
    }

    private static List<Comment> getDbCommentsByBookId(long bookId) {
        return switch ((int) bookId) {
            case 1 -> List.of(
                new Comment(1L, new Book(1L, "BookTitle_1", null, null), "Comment_1"),
                new Comment(2L, new Book(1L, "BookTitle_1", null, null), "Comment_2")
            );
            case 2 -> List.of(
                new Comment(3L, new Book(2L, "BookTitle_2", null, null), "Comment_3")
            );
            case 3 -> List.of(
                new Comment(4L, new Book(3L, "BookTitle_3", null, null), "Comment_4")
            );
            default -> List.of();
        };
    }    
}
