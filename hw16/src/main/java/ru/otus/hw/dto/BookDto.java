package ru.otus.hw.dto;

import java.util.Set;

public record BookDto(
    long id,
    String title,
    AuthorDto author,
    Set<GenreDto> genres
) {
    public boolean containsGenreById(long genreId) {
        return genres.stream()
            .anyMatch(genre -> genre.id() == genreId);
    }
}
