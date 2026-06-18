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
import ru.otus.hw.dto.CreateGenreRequestDto;
import ru.otus.hw.dto.UpdateGenreRequestDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

@SpringBootTest
class GenreServiceResilienceTest {

    @Value("${resilience4j.retry.instances.genreService.maxAttempts}")
    private int retryMaxAttempts;

    @Value("${resilience4j.circuitbreaker.instances.genreService.minimumNumberOfCalls}")
    private int cbMinCallsToOpen;

    @Autowired
    private GenreService genreService;

    @MockitoBean
    private GenreRepository genreRepository;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeEach
    void setUp() {
        circuitBreakerRegistry.circuitBreaker("genreService").reset();
    }

    @Test
    void shouldRetryFindAllAndReturnFallback() {
        when(genreRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        var result = genreService.findAll();

        assertThat(result).isEmpty();
        verify(genreRepository, times(retryMaxAttempts)).findAll();
    }

    @Test
    void shouldRetryFindByIdAndThrowFromFallback() {
        when(genreRepository.findById(1L)).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> genreService.findById(1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to find genre by id 1");

        verify(genreRepository, times(retryMaxAttempts)).findById(1L);
    }

    @Test
    @SuppressWarnings("null")
    void shouldNotRetryInsertAndThrowFromFallback() {
        when(genreRepository.save(any(Genre.class))).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> genreService.insert(new CreateGenreRequestDto("New Genre")))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to insert genre");

        verify(genreRepository, times(1)).save(any(Genre.class));
    }

    @Test
    void shouldRetryUpdateAndThrowFromFallback() {
        when(genreRepository.findById(1L)).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> genreService.update(1L, new UpdateGenreRequestDto("Updated Genre")))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to update genre with id 1");

        verify(genreRepository, times(retryMaxAttempts)).findById(1L);
    }

    @Test
    void shouldRetryDeleteAndThrowFromFallback() {
        doThrow(new RuntimeException("DB error")).when(genreRepository).deleteById(1L);

        assertThatThrownBy(() -> genreService.deleteById(1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to delete genre with id 1");

        verify(genreRepository, times(retryMaxAttempts)).deleteById(1L);
    }

    @Test
    void shouldOpenCircuitBreakerAndSkipRepositoryOnNextCall() {
        when(genreRepository.findAll()).thenThrow(new RuntimeException("DB error"));
        var cb = circuitBreakerRegistry.circuitBreaker("genreService");

        for (int i = 0; i < cbMinCallsToOpen; i++) {
            genreService.findAll();
        }
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        clearInvocations(genreRepository);
        genreService.findAll();
        verify(genreRepository, never()).findAll();
    }
}
