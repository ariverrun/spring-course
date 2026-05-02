package ru.otus.hw.dto;

public record AuthorDto(Long id, String fullName) implements IdentifiableDto {
    @Override
    public Long getId() {
        return id;
    }
}
