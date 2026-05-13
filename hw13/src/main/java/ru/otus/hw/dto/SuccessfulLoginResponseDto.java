package ru.otus.hw.dto;

public record SuccessfulLoginResponseDto(
    boolean success,
    String message
) {
}
