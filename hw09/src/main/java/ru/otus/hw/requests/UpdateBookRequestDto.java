package ru.otus.hw.requests;

import java.util.Set;

public record UpdateBookRequestDto(String title, Long authorId, Set<Long> genreIds) {
    
}
