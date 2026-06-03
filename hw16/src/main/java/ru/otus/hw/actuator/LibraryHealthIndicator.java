package ru.otus.hw.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

@Component
@RequiredArgsConstructor
public class LibraryHealthIndicator implements HealthIndicator {

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final CommentRepository commentRepository;

    @Override
    public Health health() {
        long books = bookRepository.count();
        long authors = authorRepository.count();
        long genres = genreRepository.count();
        long comments = commentRepository.count();

        return Health.up()
                .withDetail("books", books)
                .withDetail("authors", authors)
                .withDetail("genres", genres)
                .withDetail("comments", comments)
                .build();
    }
}
