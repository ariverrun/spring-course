package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.CreateAuthorRequestDto;
import ru.otus.hw.dto.UpdateAuthorRequestDto;

public interface AuthorService {
    Flux<AuthorDto> findAll();

    Mono<AuthorDto> findById(long id);

    Mono<AuthorDto> insert(CreateAuthorRequestDto dto);

    Mono<AuthorDto> update(long id, UpdateAuthorRequestDto dto);

    Mono<Void> deleteById(long id);
}