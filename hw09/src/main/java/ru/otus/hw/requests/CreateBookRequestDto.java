package ru.otus.hw.requests;

import java.util.Set;

public record CreateBookRequestDto(String title, Long authorId, Set<Long> genreIds) {
    
}
