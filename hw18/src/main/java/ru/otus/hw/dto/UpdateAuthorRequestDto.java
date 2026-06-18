package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateAuthorRequestDto(
    @NotBlank
    @Size(min = 2, max = 20)
    String fullName
) {    
}
