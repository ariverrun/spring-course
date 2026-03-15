package ru.otus.hw.commands;

import java.util.stream.Collectors;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.services.CommentService;

@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {

    private final CommentService commentService;

    private final CommentConverter commentConverter;
    
    @ShellMethod(value = "Insert comment", key = "cins")
    public String insertComment(String text, Long bookId) {
        var savedComment = commentService.insert(text, bookId);
        return commentConverter.commentToString(savedComment);
    }

    @ShellMethod(value = "Find comments by book id", key = "cbbid")
    public String findCommentsByBookId(Long bookId) {
        return commentService.findByBookId(bookId).stream()
                .map(commentConverter::commentToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }


    @ShellMethod(value = "Find comment by id", key = "cbid")
    public String findCommentById(long id) {
        return commentService.findById(id)
                .map(commentConverter::commentToString)
                .orElse("Comment with id %d not found".formatted(id));
    }   

    @ShellMethod(value = "Update comment", key = "cupd")
    public String updateComment(long id, String text, Long bookId) {
        var savedComment = commentService.update(id, text, bookId);
        return commentConverter.commentToString(savedComment);
    }

    @ShellMethod(value = "Delete comment by id", key = "cdel")
    public void deleteComment(long id) {
        commentService.deleteById(id);
    }

}
