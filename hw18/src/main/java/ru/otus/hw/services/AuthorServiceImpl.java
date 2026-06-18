package ru.otus.hw.services;

import java.util.List;

import org.springframework.stereotype.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.CreateAuthorRequestDto;
import ru.otus.hw.dto.UpdateAuthorRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    @Override
    @CircuitBreaker(
        name = "authorService",
        fallbackMethod = "findAllFallback"
    )
    @Retry(name = "authorService")
    public List<AuthorDto> findAll() {   
        return authorRepository.findAll().stream()
            .map(a -> mapAuthorToDto(a))
            .toList();
    }

    @Override
    @Retry(name = "authorService")
    @CircuitBreaker(
        name = "authorService",
        fallbackMethod = "findByIdFallback"
    )
    public AuthorDto findById(long id) {
        var author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(id)));
        return mapAuthorToDto(author);
    }

    @Override
    @Transactional
    @CircuitBreaker(
        name = "authorService",
        fallbackMethod = "insertFallback"
    )
    public AuthorDto insert(CreateAuthorRequestDto dto) {
        var author = new Author(0, dto.fullName());
        return mapAuthorToDto(authorRepository.save(author));
    }

    @Override
    @Transactional
    @Retry(name = "authorService")
    @CircuitBreaker(
        name = "authorService",
        fallbackMethod = "updateFallback"
    )
    public AuthorDto update(long id, UpdateAuthorRequestDto dto) {
        var author = authorRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(id)));
        author.setFullName(dto.fullName());
        return mapAuthorToDto(authorRepository.save(author));
    }

    @Override
    @Transactional
    @Retry(name = "authorService")
    @CircuitBreaker(
        name = "authorService",
        fallbackMethod = "deleteFallback"
    )
    public void deleteById(long id) {
        authorRepository.deleteById(id);
    }

    public List<AuthorDto> findAllFallback(Throwable ex) {
        return List.of();
    }

    public AuthorDto findByIdFallback(long id, Throwable ex) {
        throw new RuntimeException("Failed to find author by id %d".formatted(id), ex);
    }

    public AuthorDto insertFallback(CreateAuthorRequestDto dto, Throwable ex) {
        throw new RuntimeException("Failed to insert author", ex);
    }

    public AuthorDto updateFallback(long id, UpdateAuthorRequestDto dto, Throwable ex) {
        throw new RuntimeException("Failed to update author with id %d".formatted(id), ex);
    }

    public void deleteFallback(long id, Throwable ex) {
        throw new RuntimeException("Failed to delete author with id %d".formatted(id), ex);
    }

    private AuthorDto mapAuthorToDto(Author author) {
        return new AuthorDto(author.getId(), author.getFullName());
    }    
}
