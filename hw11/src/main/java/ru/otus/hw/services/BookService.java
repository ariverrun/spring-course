package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CreateBookRequestDto;
import ru.otus.hw.dto.UpdateBookRequestDto;

public interface BookService {
    Mono<BookDto> findById(long id);

    Flux<BookDto> findAll();

    Mono<BookDto> insert(CreateBookRequestDto dto);

    Mono<BookDto> update(long id, UpdateBookRequestDto dto);

    Mono<Void> deleteById(long id);
}
