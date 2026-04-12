package ru.otus.hw.services;

import java.util.List;

import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CreateCommentDto;
import ru.otus.hw.dto.UpdateCommentDto;
import ru.otus.hw.models.Comment;

public interface CommentService {
    
    Comment insert(CreateCommentDto dto);

    Comment update(UpdateCommentDto dto);

    List<Comment> findByBookId(Long bookId);

    CommentDto findById(long id);

    void deleteById(long id);
}
