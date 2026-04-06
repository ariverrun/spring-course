package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;

import ru.otus.hw.models.Comment;

public interface CommentService {
    
    Comment insert(String text, Long bookId);

    Comment update(long id, String text, Long bookId);

    List<Comment> findByBookId(Long bookId);

    Optional<Comment> findById(long id);

    void deleteById(long id);

    Comment getById(long id);
}
