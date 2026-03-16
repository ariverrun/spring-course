package ru.otus.hw.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import ru.otus.hw.models.Comment;

@DataJpaTest
@Import({CommentRepositoryJpa.class, BookRepositoryJpa.class})
public class CommentRepositoryJpaTest {
 
    private static final long FIRST_COMMENT_ID = 1L;
    private static final long SECOND_COMMENT_ID = 2L;
    private static final long THIRD_COMMENT_ID = 3L;
    private static final long FOURTH_COMMENT_ID = 4L;
    private static final long FIFTH_COMMENT_ID = 5L;
    private static final long FIRST_BOOK_ID = 1L;

    @Autowired
    private CommentRepositoryJpa repositoryJpa;

    @Autowired
    private TestEntityManager em;

    @Test
    void shouldFindExpectedCommentById() {
        var optionalActualComment = repositoryJpa.findById(FIRST_COMMENT_ID);
        var expectedComment = em.find(Comment.class, FIRST_COMMENT_ID);
        assertThat(optionalActualComment).isPresent().get()
            .usingRecursiveComparison()
            .isEqualTo(expectedComment);
    }

    @Test
    void shouldFindCommentsByBookId() {
        var actualComments = repositoryJpa.findByBookId(FIRST_BOOK_ID);
        var expectedComments = List.of(
            em.find(Comment.class, FIRST_COMMENT_ID),
            em.find(Comment.class, SECOND_COMMENT_ID)
        );

        assertThat(actualComments).isNotEmpty()
            .hasSameSizeAs(expectedComments);

        assertThat(actualComments)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrderElementsOf(expectedComments);
    }

    @Test
    void shouldInsertNewComment() {
        var newComment = new Comment();
        newComment.setText("Comment_5");

        assertThat(em.find(Comment.class, FIFTH_COMMENT_ID)).isNull();

        var savedComment = repositoryJpa.save(newComment);
        assertThat(savedComment.getId()).isEqualTo(FIFTH_COMMENT_ID);

        savedComment = em.find(Comment.class, FIFTH_COMMENT_ID);

        assertThat(savedComment)
            .usingRecursiveComparison()
            .isEqualTo(newComment);        
    }

    @Test
    void shouldUpdateComment() {
        var comment = em.find(Comment.class, THIRD_COMMENT_ID);

        var newText = "Comment_3.2";

        comment.setText(newText);

        repositoryJpa.save(comment);

        comment = em.find(Comment.class, THIRD_COMMENT_ID);

        assertThat(comment.getText()).isEqualTo(newText);
    }

    @Test
    void shouldDeleteComment() {
        repositoryJpa.deleteById(FOURTH_COMMENT_ID);
        assertThat(em.find(Comment.class, FOURTH_COMMENT_ID)).isNull();
    }  

}
