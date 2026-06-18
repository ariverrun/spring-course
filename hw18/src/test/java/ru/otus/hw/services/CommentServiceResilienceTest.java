package ru.otus.hw.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import ru.otus.hw.dto.CreateCommentDto;
import ru.otus.hw.dto.UpdateCommentDto;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@SpringBootTest
class CommentServiceResilienceTest {

    @Value("${resilience4j.retry.instances.commentService.maxAttempts}")
    private int retryMaxAttempts;

    @Value("${resilience4j.circuitbreaker.instances.commentService.minimumNumberOfCalls}")
    private int cbMinCallsToOpen;

    @Autowired
    private CommentService commentService;

    @MockitoBean
    private CommentRepository commentRepository;

    @MockitoBean
    private BookRepository bookRepository;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeEach
    void setUp() {
        circuitBreakerRegistry.circuitBreaker("commentService").reset();
    }

    @Test
    void shouldRetryFindByBookIdAndReturnFallback() {
        when(commentRepository.findByBookId(1L)).thenThrow(new RuntimeException("DB error"));

        var result = commentService.findByBookId(1L);

        assertThat(result).isEmpty();
        verify(commentRepository, times(retryMaxAttempts)).findByBookId(1L);
    }

    @Test
    void shouldRetryFindByIdAndThrowFromFallback() {
        when(commentRepository.findById(1L)).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> commentService.findById(1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to find comment by id 1");

        verify(commentRepository, times(retryMaxAttempts)).findById(1L);
    }

    @Test
    void shouldNotRetryInsertAndThrowFromFallback() {
        when(bookRepository.findById(anyLong())).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> commentService.insert(new CreateCommentDto("some text", 1L)))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to insert comment");

        verify(bookRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldRetryUpdateAndThrowFromFallback() {
        when(commentRepository.findById(1L)).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> commentService.update(new UpdateCommentDto(1L, "new text")))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to update comment with id 1");

        verify(commentRepository, times(retryMaxAttempts)).findById(1L);
    }

    @Test
    void shouldRetryDeleteAndThrowFromFallback() {
        doThrow(new RuntimeException("DB error")).when(commentRepository).deleteById(1L);

        assertThatThrownBy(() -> commentService.deleteById(1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to delete comment with id 1");

        verify(commentRepository, times(retryMaxAttempts)).deleteById(1L);
    }

    @Test
    void shouldOpenCircuitBreakerAndSkipRepositoryOnNextCall() {
        when(commentRepository.findByBookId(1L)).thenThrow(new RuntimeException("DB error"));
        var cb = circuitBreakerRegistry.circuitBreaker("commentService");

        for (int i = 0; i < cbMinCallsToOpen; i++) {
            commentService.findByBookId(1L);
        }
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        clearInvocations(commentRepository);
        commentService.findByBookId(1L);
        verify(commentRepository, never()).findByBookId(1L);
    }
}
