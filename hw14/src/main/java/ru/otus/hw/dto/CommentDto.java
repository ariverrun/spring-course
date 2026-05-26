package ru.otus.hw.dto;

public record CommentDto(
    String id,
    String text,
    String bookId,
    String bookTitle
) {
}
