package ru.otus.hw.repositories;

import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.models.Book;

@RequiredArgsConstructor
@Repository
public class BooksRepositoryCustomImpl implements BooksRepositoryCustom {
    
    private final MongoOperations mongoOperations;

    public List<Book> findBooksWithGenre(String genreId) {
        Query query = Query.query(Criteria.where("genres.id").is(genreId));
        return mongoOperations.find(query, Book.class);
    }
}
