package ru.otus.hw.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import ru.otus.hw.dto.CreateAuthorRequestDto;
import ru.otus.hw.dto.UpdateAuthorRequestDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

@SpringBootTest
class AuthorServiceResilienceTest {

    @Value("${resilience4j.retry.instances.authorService.maxAttempts}")
    private int retryMaxAttempts;

    @Value("${resilience4j.circuitbreaker.instances.authorService.minimumNumberOfCalls}")
    private int cbMinCallsToOpen;

    @Autowired
    private AuthorService authorService;

    @MockitoBean
    private AuthorRepository authorRepository;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeEach
    void setUp() {
        circuitBreakerRegistry.circuitBreaker("authorService").reset();
    }

    @Test
    void shouldRetryFindAllAndReturnFallback() {
        when(authorRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        var result = authorService.findAll();

        assertThat(result).isEmpty();
        verify(authorRepository, times(retryMaxAttempts)).findAll();
    }

    @Test
    void shouldRetryFindByIdAndThrowFromFallback() {
        when(authorRepository.findById(1L)).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> authorService.findById(1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to find author by id 1");

        verify(authorRepository, times(retryMaxAttempts)).findById(1L);
    }

    @Test
    @SuppressWarnings("null")
    void shouldNotRetryInsertAndThrowFromFallback() {
        when(authorRepository.save(any(Author.class))).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> authorService.insert(new CreateAuthorRequestDto("Test Author")))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to insert author");

        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    void shouldRetryUpdateAndThrowFromFallback() {
        when(authorRepository.findById(1L)).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> authorService.update(1L, new UpdateAuthorRequestDto("New Name")))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to update author with id 1");

        verify(authorRepository, times(retryMaxAttempts)).findById(1L);
    }

    @Test
    void shouldRetryDeleteAndThrowFromFallback() {
        doThrow(new RuntimeException("DB error")).when(authorRepository).deleteById(1L);

        assertThatThrownBy(() -> authorService.deleteById(1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to delete author with id 1");

        verify(authorRepository, times(retryMaxAttempts)).deleteById(1L);
    }

    @Test
    void shouldOpenCircuitBreakerAndSkipRepositoryOnNextCall() {
        when(authorRepository.findAll()).thenThrow(new RuntimeException("DB error"));
        var cb = circuitBreakerRegistry.circuitBreaker("authorService");

        for (int i = 0; i < cbMinCallsToOpen; i++) {
            authorService.findAll();
        }
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        clearInvocations(authorRepository);
        authorService.findAll();
        verify(authorRepository, never()).findAll();
    }
}
