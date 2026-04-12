package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;

import ru.otus.hw.dto.CreateCommentDto;
import ru.otus.hw.dto.UpdateCommentDto;
import ru.otus.hw.models.Comment;

public interface CommentService {
    
    Comment insert(CreateCommentDto dto);

    Comment update(UpdateCommentDto dto);

    List<Comment> findByBookId(Long bookId);

    Optional<Comment> findById(long id);

    void deleteById(long id);

    Comment getById(long id);
}
