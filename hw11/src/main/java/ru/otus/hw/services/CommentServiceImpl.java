package ru.otus.hw.services;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CreateCommentDto;
import ru.otus.hw.dto.UpdateCommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;
    
    @Override
    public Mono<CommentDto> insert(CreateCommentDto dto) {
        return bookRepository.findById(dto.bookId())
            .switchIfEmpty(Mono.error(new EntityNotFoundException("Book with id %s not found".formatted(dto.bookId()))))
            .flatMap(book -> {
                Comment comment = new Comment();
                comment.setText(dto.text());
                comment.setBookId(book.getId());
                return commentRepository.save(comment);
            })
            .map(commentMapper::mapCommentToDto);
    }

    @Override
    public Mono<CommentDto> update(UpdateCommentDto dto) {
        return commentRepository.findById(dto.id())
            .switchIfEmpty(Mono.error(new EntityNotFoundException("Comment with id %s not found".formatted(dto.id()))))
            .flatMap(comment -> {
                comment.setText(dto.text());
                return commentRepository.save(comment);
            })
            .map(commentMapper::mapCommentToDto);
    }

    @Override
    public Flux<CommentDto> findByBookId(String bookId) {
        return commentRepository.findByBookId(bookId)
            .map(commentMapper::mapCommentToDto);
    }

    @Override
    public Mono<CommentDto> findById(String id) {
        return commentRepository.findById(id)
            .switchIfEmpty(Mono.error(new EntityNotFoundException("Comment with id %s not found".formatted(id))))
            .map(commentMapper::mapCommentToDto);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return commentRepository.deleteById(id);
    } 
}