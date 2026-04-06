package ru.otus.hw.listeners;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;

@Component
@RequiredArgsConstructor
public class AuthorCascadeDeleteEventListener extends AbstractMongoEventListener<Author> {
    
    private final MongoOperations mongoOperations;
    
    @Override
    public void onBeforeDelete(BeforeDeleteEvent<Author> event) {
        Document document = event.getSource();
        
        Object authorId = document.get("_id");
        
        if (authorId == null) {
            return;
        }
        
        String authorIdStr = authorId.toString();
        
        Query query = new Query(Criteria.where("author.id").is(authorIdStr));
        mongoOperations.remove(query, Book.class);        
    }
}