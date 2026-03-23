package ru.otus.hw.repositories;

import java.util.List;
import java.util.Optional;

import ru.otus.hw.models.Comment;

public interface CommentRepository {

    Comment save(Comment comment);

    List<Comment> findByBookId(Long bookId);

    Optional<Comment> findById(long id);

    void deleteById(long id);
}
