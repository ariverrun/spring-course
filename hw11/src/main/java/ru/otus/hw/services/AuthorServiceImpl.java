package ru.otus.hw.services;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
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
        return Mono.fromCallable(authorRepository::findAll)
                .flatMapMany(Flux::fromIterable)
                .map(this::mapAuthorToDto)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<AuthorDto> findById(long id) {
        return Mono.fromCallable(() -> authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(id))))
                .map(this::mapAuthorToDto)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private AuthorDto mapAuthorToDto(Author author) {
        return new AuthorDto(author.getId(), author.getFullName());
    }

    @Override
    @Transactional
    public Mono<AuthorDto> insert(CreateAuthorRequestDto dto) {
        return Mono.fromCallable(() -> {
            var author = new Author(0, dto.fullName());
            return authorRepository.save(author);
        })
        .map(this::mapAuthorToDto)
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<AuthorDto> update(long id, UpdateAuthorRequestDto dto) {
        return Mono.fromCallable(() -> {
            var author = authorRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(id)));
            author.setFullName(dto.fullName());
            return authorRepository.save(author);
        })
        .map(this::mapAuthorToDto)
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(long id) {
        return Mono.fromRunnable(() -> authorRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}