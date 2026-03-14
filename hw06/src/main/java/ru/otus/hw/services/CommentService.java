package ru.otus.hw.services;

import java.util.List;

import ru.otus.hw.models.Comment;

public interface CommentService {
    
    Comment insert(String text, Long bookId);

    Comment update(long id, String text, Long bookId);

    List<Comment> findAll();
}
