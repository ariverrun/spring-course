package ru.otus.hw.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepositoryJpa;
import ru.otus.hw.repositories.CommentRepositoryJpa;

@DataJpaTest
@Transactional(propagation = Propagation.NEVER)
@Import({CommentServiceImpl.class, BookRepositoryJpa.class, CommentRepositoryJpa.class})
public class CommentServiceImplTest {

    private static final long FIRST_BOOK_ID = 1L;
    private static final long FIRST_COMMENT_ID = 1L;

    @Autowired
    private CommentServiceImpl commentService;
    
    @Test
    void shouldFindAllCommentsndAllowAccessToLazyPropertiesWithoutTransaction() {
        List<Comment> comments = commentService.findByBookId(FIRST_BOOK_ID);
        assertThat(TransactionSynchronizationManager.isActualTransactionActive())
            .isFalse();
        assertThat(comments).isNotEmpty();

        for (Comment comment : comments) {
            assertCommentLazyPropertiesAreAccessible(comment);
        }
    }

    @Test
    void shouldFindCommentByIdAndAllowAccessToLazyPropertiesWithoutTransaction() {
        var optionalComment = commentService.findById(FIRST_COMMENT_ID);
        assertThat(TransactionSynchronizationManager.isActualTransactionActive())
            .isFalse();
        assertThat(optionalComment).isPresent();
        assertCommentLazyPropertiesAreAccessible(optionalComment.get());
    }

    private void assertCommentLazyPropertiesAreAccessible(Comment comment) {
        assertDoesNotThrow(() -> {
            comment.getBook().getTitle();
            comment.getBook().getAuthor().getFullName();
            comment.getBook().getGenres().size();
        });
    }
}
