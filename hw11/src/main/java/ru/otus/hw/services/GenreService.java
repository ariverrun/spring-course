package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CreateGenreRequestDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.UpdateGenreRequestDto;

public interface GenreService {
    Flux<GenreDto> findAll();

    Mono<GenreDto> findById(long id);

    Mono<GenreDto> insert(CreateGenreRequestDto dto);

    Mono<GenreDto> update(long id, UpdateGenreRequestDto dto);

    Mono<Void> deleteById(long id);
}
