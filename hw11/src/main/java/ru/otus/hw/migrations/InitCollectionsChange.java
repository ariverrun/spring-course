package ru.otus.hw.migrations;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

@ChangeUnit(id = "init_collections", order = "001")
@Component
public class InitCollectionsChange {
    
    private final ReactiveMongoTemplate mongoTemplate;
    
    public InitCollectionsChange(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    @Execution
    public void execute() {
        createCollectionsIfNotExist()
            .then(insertAuthors())
            .then(insertGenres())
            .then(insertBooks())
            .then(insertComments())
            .block();
    }
    
    private Mono<Void> createCollectionsIfNotExist() {
        return Mono.when(
            createCollectionIfNotExists("authors"),
            createCollectionIfNotExists("genres"),
            createCollectionIfNotExists("books"),
            createCollectionIfNotExists("comments")
        );
    }
    
    private Mono<Void> createCollectionIfNotExists(String collectionName) {
        return mongoTemplate.collectionExists(collectionName)
            .flatMap(exists -> exists ? Mono.empty() : mongoTemplate.createCollection(collectionName).then());
    }
    
    private Mono<Void> insertAuthors() {
        List<Author> authors = List.of(
            new Author("1", "Author_1"),
            new Author("2", "Author_2"),
            new Author("3", "Author_3")
        );
        return mongoTemplate.insertAll(authors).then();
    }
    
    private Mono<Void> insertGenres() {
        List<Genre> genres = List.of(
            new Genre("1", "Genre_1"),
            new Genre("2", "Genre_2"),
            new Genre("3", "Genre_3"),
            new Genre("4", "Genre_4"),
            new Genre("5", "Genre_5"),
            new Genre("6", "Genre_6")
        );
        return mongoTemplate.insertAll(genres).then();
    }
    
    private Mono<Void> insertBooks() {
        return Mono.zip(
            mongoTemplate.findById("1", Author.class),
            mongoTemplate.findById("2", Author.class),
            mongoTemplate.findById("3", Author.class)
        ).zipWith(
            Flux.just("1", "2", "3", "4", "5", "6")
                .flatMap(id -> mongoTemplate.findById(id, Genre.class)).collectList()
        ).flatMap(tuple -> {
            Author author1 = tuple.getT1().getT1();
            Author author2 = tuple.getT1().getT2();
            Author author3 = tuple.getT1().getT3();
            List<Genre> genres = tuple.getT2();
            Book book1 = new Book("1", "BookTitle_1", author1, new ArrayList<>());
            Book book2 = new Book("2", "BookTitle_2", author2, new ArrayList<>());
            Book book3 = new Book("3", "BookTitle_3", author3, new ArrayList<>());
            book1.getGenres().add(genres.get(0));
            book1.getGenres().add(genres.get(1));
            book2.getGenres().add(genres.get(2));
            book2.getGenres().add(genres.get(3));
            book3.getGenres().add(genres.get(4));
            book3.getGenres().add(genres.get(5));
            return mongoTemplate.insertAll(List.of(book1, book2, book3)).then();
        });
    }
    
    private Mono<Void> insertComments() {
        List<Comment> comments = List.of(
            new Comment("1", "1", "Comment_1"),
            new Comment("2", "1", "Comment_2"),
            new Comment("3", "2", "Comment_3"),
            new Comment("4", "3", "Comment_4")
        );
        return mongoTemplate.insertAll(comments).then();
    }
    
    @RollbackExecution
    public void rollback() {
        dropCollectionIfExists("comments")
            .then(dropCollectionIfExists("books"))
            .then(dropCollectionIfExists("genres"))
            .then(dropCollectionIfExists("authors"))
            .block();
    }
    
    private Mono<Void> dropCollectionIfExists(String collectionName) {
        return mongoTemplate.collectionExists(collectionName)
            .flatMap(exists -> exists ? mongoTemplate.dropCollection(collectionName) : Mono.empty());
    }
}