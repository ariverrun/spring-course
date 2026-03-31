package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
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
    public Comment insert(String text, String bookId) {
        var comment = new Comment(null, getBookById(bookId), text);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment update(String id, String text, String bookId) {
        var comment = commentRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Comment with id %s not found".formatted(id)));
        comment.setText(text);
        comment.setBook(getBookById(bookId));
        return commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findByBookId(String bookId) {
        var comments = commentRepository.findByBookId(bookId);
        return comments;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> findById(String id) {
        return commentRepository.findById(id);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        commentRepository.deleteById(id);
    }

    private Book getBookById(String bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookId)));
    } 
}
