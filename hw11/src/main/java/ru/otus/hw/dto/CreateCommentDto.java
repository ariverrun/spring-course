package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCommentDto(
    @NotBlank
    @Size(min = 2, max = 1024)
    String text,
    @NotNull
    String bookId
) {
    
}
