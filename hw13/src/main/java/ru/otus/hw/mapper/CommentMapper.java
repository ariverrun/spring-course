package ru.otus.hw.mapper;

import org.springframework.stereotype.Component;

import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;

@Component
public class CommentMapper {
    public CommentDto mapCommentToDto(Comment comment) {
        return new CommentDto(
            comment.getId(),
            comment.getText(),
            comment.getBook().getId(),
            comment.getBook().getTitle()
        );        
    }
}
