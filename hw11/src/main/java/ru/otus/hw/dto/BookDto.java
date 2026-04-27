package ru.otus.hw.dto;

import java.util.Set;

public record BookDto(
    String id,
    String title,
    AuthorDto author,
    Set<GenreDto> genres
) {
    public boolean containsGenreById(String genreId) {
        return genres.stream()
            .anyMatch(genre -> genre.id() == genreId);
    }
}
