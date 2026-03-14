package ru.otus.hw.repositories;

import java.util.List;

import ru.otus.hw.models.Comment;

public interface CommentRepository {

    Comment save(Comment comment);

    List<Comment> findAll();

    List<Comment> findByBookId(Long bookId);
}
