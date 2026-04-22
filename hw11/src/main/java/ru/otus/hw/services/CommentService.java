package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CreateCommentDto;
import ru.otus.hw.dto.UpdateCommentDto;

public interface CommentService {
    
    Mono<CommentDto> insert(CreateCommentDto dto);

    Mono<CommentDto> update(UpdateCommentDto dto);

    Flux<CommentDto> findByBookId(String bookId);

    Mono<CommentDto> findById(String id);

    Mono<Void> deleteById(String id);
}
