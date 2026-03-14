package ru.otus.hw.services;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    public Comment insert(String text, Long bookId) {
        return save(0, text, bookId);
    }

    @Override
    @Transactional
    public Comment update(long id, String text, Long bookId) {
        return save(id, text, bookId);
    }

    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Override
    public List<Comment> findBookId(Long bookId) {
        return commentRepository.findByBookId(bookId);
    }

    private Comment save(long id, String text, Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new EntityNotFoundException("Book with id %d not found".formatted(bookId));
        }

        var comment = new Comment();
        comment.setId(id);
        comment.setText(text);
        comment.setBookId(bookId);

        return commentRepository.save(comment);
    }
}
