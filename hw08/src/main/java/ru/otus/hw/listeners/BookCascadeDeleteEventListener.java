package ru.otus.hw.listeners;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

@Component
@RequiredArgsConstructor
public class BookCascadeDeleteEventListener extends AbstractMongoEventListener<Book> {
    
    private final MongoOperations mongoOperations;
    
    @Override
    public void onBeforeDelete(BeforeDeleteEvent<Book> event) {        
        Document document = event.getSource();
        
        Object bookId = document.get("_id");
        
        if (bookId == null) {
            return;
        }
        
        String bookIdStr = bookId.toString();
        Query deleteQuery = new Query(Criteria.where("book._id").is(bookIdStr));
        mongoOperations.remove(deleteQuery, Comment.class).getDeletedCount();
    }
}