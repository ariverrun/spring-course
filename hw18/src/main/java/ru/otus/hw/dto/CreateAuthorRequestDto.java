package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAuthorRequestDto(
    @NotBlank
    @Size(min = 2, max = 20)
    String fullName
) {    
}
