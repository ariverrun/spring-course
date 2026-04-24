package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;

public interface BookRepository extends ReactiveMongoRepository<Book, String> {
    
    Mono<Void> deleteByAuthor(Author author);

    @Query("{ 'genres._id' : ?0 }")
    Flux<Book> findBooksByGenreId(String genreId);
}