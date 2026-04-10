package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;

import ru.otus.hw.dto.CreateCommentRequestDto;
import ru.otus.hw.models.Comment;

public interface CommentService {
    
    Comment insert(CreateCommentRequestDto dto);

    Comment update(long id, String text, Long bookId);

    List<Comment> findByBookId(Long bookId);

    Optional<Comment> findById(long id);

    void deleteById(long id);

    Comment getById(long id);
}
