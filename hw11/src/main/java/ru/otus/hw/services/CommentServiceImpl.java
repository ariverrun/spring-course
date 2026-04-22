package ru.otus.hw.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CreateCommentDto;
import ru.otus.hw.dto.UpdateCommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;
    
    @Override
    @Transactional
    public Mono<CommentDto> insert(CreateCommentDto dto) {
        return Mono.fromCallable(() -> bookRepository.findById(dto.bookId())
            .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(dto.bookId()))))
            .flatMap(book -> Mono.fromCallable(() -> {
                var comment = new Comment(0, book, dto.text());
                return commentRepository.save(comment);
            }))
            .map(this::mapCommentToDto)
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<CommentDto> update(UpdateCommentDto dto) {
        return Mono.fromCallable(() -> {
            var comment = commentRepository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %d not found".formatted(dto.id())));
            comment.setText(dto.text());
            return commentRepository.save(comment);
        })
        .map(this::mapCommentToDto)
        .subscribeOn(Schedulers.boundedElastic());        
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CommentDto> findByBookId(Long bookId) {
        return Mono.fromCallable(() -> commentRepository.findByBookId(bookId))
                .flatMapMany(Flux::fromIterable)
                .map(this::mapCommentToDto)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CommentDto> findById(long id) {
        return Mono.fromCallable(() -> commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %d not found".formatted(id))))
                .map(this::mapCommentToDto)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private CommentDto mapCommentToDto(Comment comment) {
        return new CommentDto(
            comment.getId(),
            comment.getText(),
            comment.getBook().getId(),
            comment.getBook().getTitle()
        );        
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(long id) {
        return Mono.fromRunnable(() -> commentRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
