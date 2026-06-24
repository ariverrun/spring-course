package ru.otus.hw.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CreateCommentDto;
import ru.otus.hw.dto.UpdateCommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;
    
    @Override
    @CircuitBreaker(name = "commentService", fallbackMethod = "insertFallback")
    @Transactional
    public CommentDto insert(CreateCommentDto dto) {
        var comment = new Comment(0, getBookById(dto.bookId()), dto.text());
        return mapCommentToDto(commentRepository.save(comment));
    }

    @Override
    @Retry(name = "commentService")
    @CircuitBreaker(name = "commentService", fallbackMethod = "updateFallback")
    @Transactional
    public CommentDto update(UpdateCommentDto dto) {
        var comment = commentRepository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %d not found".formatted(dto.id())));
        comment.setText(dto.text());
        return mapCommentToDto(commentRepository.save(comment));
    }

    @Override
    @Retry(name = "commentService")
    @CircuitBreaker(name = "commentService", fallbackMethod = "findByBookIdFallback")
    @Transactional(readOnly = true)
    public List<CommentDto> findByBookId(Long bookId) {
        return commentRepository.findByBookId(bookId).stream()
            .map(c -> mapCommentToDto(c))
            .toList();
    }

    @Override
    @Retry(name = "commentService")
    @CircuitBreaker(name = "commentService", fallbackMethod = "findByIdFallback")
    @Transactional(readOnly = true)
    public CommentDto findById(long id) {
        var comment = commentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Comment with id %d not found".formatted(id)));
        return mapCommentToDto(comment);
    }

    @Override
    @Retry(name = "commentService")
    @CircuitBreaker(name = "commentService", fallbackMethod = "deleteFallback")
    @Transactional
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }

    public List<CommentDto> findByBookIdFallback(Long bookId, Throwable ex) {
        return List.of();
    }

    public CommentDto findByIdFallback(long id, Throwable ex) {
        throw new RuntimeException("Failed to find comment by id %d".formatted(id), ex);
    }

    public CommentDto insertFallback(CreateCommentDto dto, Throwable ex) {
        throw new RuntimeException("Failed to insert comment", ex);
    }

    public CommentDto updateFallback(UpdateCommentDto dto, Throwable ex) {
        throw new RuntimeException("Failed to update comment with id %d".formatted(dto.id()), ex);
    }

    public void deleteFallback(long id, Throwable ex) {
        throw new RuntimeException("Failed to delete comment with id %d".formatted(id), ex);
    }

    private CommentDto mapCommentToDto(Comment comment) {
        return new CommentDto(
            comment.getId(),
            comment.getText(),
            comment.getBook().getId(),
            comment.getBook().getTitle()
        );
    }

    private Book getBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));
    }
}
