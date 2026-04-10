package ru.otus.hw.dto;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateBookRequestDto(
    @NotBlank
    @Size(min = 2, max = 20)
    String title,
    @NotNull
    Long authorId,
    @NotEmpty
    Set<Long> genreIds
) {
}
