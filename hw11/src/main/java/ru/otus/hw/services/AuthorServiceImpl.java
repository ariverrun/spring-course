package ru.otus.hw.services;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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
    public Flux<AuthorDto> findAll() {
        return authorRepository.findAll()
            .map(this::mapAuthorToDto);
    }

    @Override
    public Mono<AuthorDto> findById(String id) {
        return authorRepository.findById(id)
            .map(this::mapAuthorToDto)
            .switchIfEmpty(Mono.error(new EntityNotFoundException("Author with id %s not found".formatted(id))));
    }

    private AuthorDto mapAuthorToDto(Author author) {
        return new AuthorDto(author.getId(), author.getFullName());
    }

    @Override
    public Mono<AuthorDto> insert(CreateAuthorRequestDto dto) {
        var author = new Author(null, dto.fullName());
        return authorRepository.save(author)
            .map(this::mapAuthorToDto);
    }

    @Override
    public Mono<AuthorDto> update(String id, UpdateAuthorRequestDto dto) {
        return authorRepository.findById(id)
            .switchIfEmpty(Mono.error(new EntityNotFoundException("Author with id %s not found".formatted(id))))
            .flatMap(author -> {
                author.setFullName(dto.fullName());
                return authorRepository.save(author);
            })
            .map(this::mapAuthorToDto);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return authorRepository.deleteById(id);
    }
}