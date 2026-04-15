package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;

import ru.otus.hw.models.Comment;

public interface CommentService {
    
    Comment insert(String text, String bookId);

    Comment update(String id, String text, String bookId);

    List<Comment> findByBookId(String bookId);

    Optional<Comment> findById(String id);

    void deleteById(String id);
}
