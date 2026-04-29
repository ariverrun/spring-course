package ru.otus.hw.dto;

public record RestApiErrorDto(
    String error,
    String message
) {
}