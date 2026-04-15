package ru.otus.hw.migrations;

import java.util.ArrayList;

import org.springframework.data.mongodb.core.MongoTemplate;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

@ChangeUnit(id = "init_collections", order = "001")
public class InitCollectionsChange {
    
    private final MongoTemplate mongoTemplate;
    
    public InitCollectionsChange(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    @Execution
    public void execute() {
        createCollectionsIfNotExist();
        insertAuthors();
        insertGenres();
        insertBooks();
        insertComments();
    }
    
    private void createCollectionsIfNotExist() {
        if (!mongoTemplate.collectionExists("authors")) {
            mongoTemplate.createCollection("authors");
        }
        
        if (!mongoTemplate.collectionExists("genres")) {
            mongoTemplate.createCollection("genres");
        }
        
        if (!mongoTemplate.collectionExists("books")) {
            mongoTemplate.createCollection("books");
        }

        if (!mongoTemplate.collectionExists("comments")) {
            mongoTemplate.createCollection("comments");
        }
    }
    
    private void insertAuthors() {
        Author author1 = new Author("1", "Author_1");
        Author author2 = new Author("2", "Author_2");
        Author author3 = new Author("3", "Author_3");
        mongoTemplate.insert(author1, "authors");
        mongoTemplate.insert(author2, "authors");
        mongoTemplate.insert(author3, "authors");
    }
    
    private void insertGenres() {
        Genre genre1 = new Genre("1", "Genre_1");
        Genre genre2 = new Genre("2", "Genre_2");
        Genre genre3 = new Genre("3", "Genre_3");
        Genre genre4 = new Genre("4", "Genre_4");
        Genre genre5 = new Genre("5", "Genre_5");
        Genre genre6 = new Genre("6", "Genre_6");
        mongoTemplate.insert(genre1, "genres");
        mongoTemplate.insert(genre2, "genres");
        mongoTemplate.insert(genre3, "genres");
        mongoTemplate.insert(genre4, "genres");
        mongoTemplate.insert(genre5, "genres");
        mongoTemplate.insert(genre6, "genres");
    }
    
    private void insertBooks() {
        Author author1 = mongoTemplate.findById("1", Author.class, "authors");
        Author author2 = mongoTemplate.findById("2", Author.class, "authors");
        Author author3 = mongoTemplate.findById("3", Author.class, "authors");
        Genre genre1 = mongoTemplate.findById("1", Genre.class, "genres");
        Genre genre2 = mongoTemplate.findById("2", Genre.class, "genres");
        Genre genre3 = mongoTemplate.findById("3", Genre.class, "genres");
        Genre genre4 = mongoTemplate.findById("4", Genre.class, "genres");
        Genre genre5 = mongoTemplate.findById("5", Genre.class, "genres");
        Genre genre6 = mongoTemplate.findById("6", Genre.class, "genres");
        Book book1 = new Book("1", "BookTitle_1", author1, new ArrayList<>());
        Book book2 = new Book("2", "BookTitle_2", author2, new ArrayList<>());
        Book book3 = new Book("3", "BookTitle_3", author3, new ArrayList<>());
        book1.addGenre(genre1);
        book1.addGenre(genre2);
        book2.addGenre(genre3);
        book2.addGenre(genre4);
        book3.addGenre(genre5);
        book3.addGenre(genre6);
        mongoTemplate.insert(book1, "books");
        mongoTemplate.insert(book2, "books");
        mongoTemplate.insert(book3, "books");
    }
    
    private void insertComments() {
        Book book1 = mongoTemplate.findById("1", Book.class, "books");
        Book book2 = mongoTemplate.findById("2", Book.class, "books");
        Book book3 = mongoTemplate.findById("3", Book.class, "books");
        Comment comment1 = new Comment("1", book1, "Comment_1");
        Comment comment2 = new Comment("2", book1, "Comment_2");
        Comment comment3 = new Comment("3", book2, "Comment_3");
        Comment comment4 = new Comment("4", book3, "Comment_4");
        mongoTemplate.insert(comment1, "comments");
        mongoTemplate.insert(comment2, "comments");
        mongoTemplate.insert(comment3, "comments");
        mongoTemplate.insert(comment4, "comments");
    }
    
    @RollbackExecution
    public void rollback() {
        dropCollectionIfExists("comments");
        dropCollectionIfExists("books");
        dropCollectionIfExists("genres");
        dropCollectionIfExists("authors");
    }
    
    private void dropCollectionIfExists(String collectionName) {
        if (mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.dropCollection(collectionName);
        }
    }
}