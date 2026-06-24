package ru.otus.hw.dto;

import java.util.Map;

public record RestApiValidationErrorDto(
    String error,
    Map<String,String> violations
) {
}