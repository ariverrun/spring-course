package ru.otus.hw.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import ru.otus.hw.models.Comment;

@DataJpaTest
@Transactional(propagation = Propagation.NEVER)
@Import(CommentServiceImpl.class)
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
    }

    @Test
    void shouldFindCommentByIdAndAllowAccessToLazyPropertiesWithoutTransaction() {
        var optionalComment = commentService.findById(FIRST_COMMENT_ID);
        assertThat(TransactionSynchronizationManager.isActualTransactionActive())
            .isFalse();
        assertThat(optionalComment).isPresent();
    }
}
