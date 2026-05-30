package ru.otus.hw.services;

import java.util.List;

import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CreateCommentDto;
import ru.otus.hw.dto.UpdateCommentDto;

public interface CommentService {

    CommentDto insert(CreateCommentDto dto);

    CommentDto update(UpdateCommentDto dto);

    List<CommentDto> findByBookId(String bookId);

    CommentDto findById(String id);

    void deleteById(String id);
}
