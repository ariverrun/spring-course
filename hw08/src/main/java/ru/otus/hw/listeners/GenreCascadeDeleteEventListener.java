package ru.otus.hw.listeners;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

@Component
@RequiredArgsConstructor
public class GenreCascadeDeleteEventListener extends AbstractMongoEventListener<Genre> {
    
    private final MongoOperations mongoOperations;
    
    @Override
    public void onBeforeDelete(BeforeDeleteEvent<Genre> event) {
        Document document = event.getSource();
        
        Object genreId = document.get("_id");
        
        if (genreId == null) {
            return;
        }
        
        String genreIdStr = genreId.toString();
        
        Query query = new Query(Criteria.where("genres.id").is(genreIdStr));
        Update update = new Update().pull("genres", Query.query(Criteria.where("id").is(genreIdStr)));
        mongoOperations.updateMulti(query, update, Book.class);        
    }
}