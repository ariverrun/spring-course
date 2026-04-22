package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CreateBookRequestDto;
import ru.otus.hw.dto.UpdateBookRequestDto;

public interface BookService {
    Mono<BookDto> findById(String id);

    Flux<BookDto> findAll();

    Mono<BookDto> insert(CreateBookRequestDto dto);

    Mono<BookDto> update(String id, UpdateBookRequestDto dto);

    Mono<Void> deleteById(String id);
}
