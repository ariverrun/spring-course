package ru.otus.hw.services;

import java.util.List;

import org.springframework.stereotype.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.dto.CreateGenreRequestDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.UpdateGenreRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    @Retry(name = "genreService")
    @CircuitBreaker(name = "genreService", fallbackMethod = "findAllFallback")
    public List<GenreDto> findAll() {
        return genreRepository.findAll().stream()
            .map(g -> mapGenreToDto(g))
            .toList();
    }

    @Override
    @Retry(name = "genreService")
    @CircuitBreaker(name = "genreService", fallbackMethod = "findByIdFallback")
    public GenreDto findById(long id) {
        var genre = genreRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Genre with id %d not found".formatted(id)));
        return mapGenreToDto(genre);
    }

    @Override
    @CircuitBreaker(name = "genreService", fallbackMethod = "insertFallback")
    @Transactional
    public GenreDto insert(CreateGenreRequestDto dto) {
        var genre = new Genre(0, dto.name());
        return mapGenreToDto(genreRepository.save(genre));
    }

    @Override
    @Retry(name = "genreService")
    @CircuitBreaker(name = "genreService", fallbackMethod = "updateFallback")
    @Transactional
    public GenreDto update(long id, UpdateGenreRequestDto dto) {
        var genre = genreRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Genre with id %d not found".formatted(id)));
        genre.setName(dto.name());
        return mapGenreToDto(genreRepository.save(genre));
    }

    @Override
    @Retry(name = "genreService")
    @CircuitBreaker(name = "genreService", fallbackMethod = "deleteFallback")
    @Transactional
    public void deleteById(long id) {
        genreRepository.deleteById(id);
    }

    public List<GenreDto> findAllFallback(Throwable ex) {
        return List.of();
    }

    public GenreDto findByIdFallback(long id, Throwable ex) {
        throw new RuntimeException("Failed to find genre by id %d".formatted(id), ex);
    }

    public GenreDto insertFallback(CreateGenreRequestDto dto, Throwable ex) {
        throw new RuntimeException("Failed to insert genre", ex);
    }

    public GenreDto updateFallback(long id, UpdateGenreRequestDto dto, Throwable ex) {
        throw new RuntimeException("Failed to update genre with id %d".formatted(id), ex);
    }

    public void deleteFallback(long id, Throwable ex) {
        throw new RuntimeException("Failed to delete genre with id %d".formatted(id), ex);
    }

    private GenreDto mapGenreToDto(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }
}
