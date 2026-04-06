package ru.otus.hw.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequestDto(
    @NotBlank
    @Size(min = 2, max = 1024)
    String text
) {    
}
