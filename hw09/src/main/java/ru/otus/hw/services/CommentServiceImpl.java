package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
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
    @Transactional
    public Comment insert(CreateCommentDto dto) {
        var comment = new Comment(0, getBookById(dto.bookId()), dto.text());
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment update(UpdateCommentDto dto) {
        var comment = commentRepository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %d not found".formatted(dto.id())));
        comment.setText(dto.text());
        comment.setBook(getBookById(dto.bookId()));
        return commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findByBookId(Long bookId) {
        var comments = commentRepository.findByBookId(bookId);
        return comments;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> findById(long id) {
        return commentRepository.findById(id);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public Comment getById(long id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException("Comment with id %d not found".formatted(id)));
    }

    private Book getBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));
    } 
}
