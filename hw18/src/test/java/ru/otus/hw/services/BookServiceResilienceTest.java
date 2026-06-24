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

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import ru.otus.hw.dto.CreateBookRequestDto;
import ru.otus.hw.dto.UpdateBookRequestDto;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

@SpringBootTest
class BookServiceResilienceTest {

    @Value("${resilience4j.retry.instances.bookService.maxAttempts}")
    private int retryMaxAttempts;

    @Value("${resilience4j.circuitbreaker.instances.bookService.minimumNumberOfCalls}")
    private int cbMinCallsToOpen;

    @Autowired
    private BookService bookService;

    @MockitoBean
    private BookRepository bookRepository;

    @MockitoBean
    private AuthorRepository authorRepository;

    @MockitoBean
    private GenreRepository genreRepository;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeEach
    void setUp() {
        circuitBreakerRegistry.circuitBreaker("bookService").reset();
    }

    @Test
    void shouldRetryFindAllAndReturnFallback() {
        when(bookRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        var result = bookService.findAll();

        assertThat(result).isEmpty();
        verify(bookRepository, times(retryMaxAttempts)).findAll();
    }

    @Test
    void shouldRetryFindByIdAndThrowFromFallback() {
        when(bookRepository.findById(1L)).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> bookService.findById(1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to find book by id 1");

        verify(bookRepository, times(retryMaxAttempts)).findById(1L);
    }

    @Test
    void shouldNotRetryInsertAndThrowFromFallback() {
        when(authorRepository.findById(anyLong())).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> bookService.insert(new CreateBookRequestDto("Title", 1L, Set.of(1L))))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to insert book");

        verify(authorRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldRetryUpdateAndThrowFromFallback() {
        when(bookRepository.findById(1L)).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> bookService.update(1L, new UpdateBookRequestDto("New Title", 1L, Set.of(1L))))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to update book with id 1");

        verify(bookRepository, times(retryMaxAttempts)).findById(1L);
    }

    @Test
    void shouldRetryDeleteAndThrowFromFallback() {
        doThrow(new RuntimeException("DB error")).when(bookRepository).deleteById(1L);

        assertThatThrownBy(() -> bookService.deleteById(1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to delete book with id 1");

        verify(bookRepository, times(retryMaxAttempts)).deleteById(1L);
    }

    @Test
    void shouldOpenCircuitBreakerAndSkipRepositoryOnNextCall() {
        when(bookRepository.findAll()).thenThrow(new RuntimeException("DB error"));
        var cb = circuitBreakerRegistry.circuitBreaker("bookService");

        for (int i = 0; i < cbMinCallsToOpen; i++) {
            bookService.findAll();
        }
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        clearInvocations(bookRepository);
        bookService.findAll();
        verify(bookRepository, never()).findAll();
    }
}
