package ru.otus.hw.repositories.document;

import org.springframework.data.mongodb.repository.MongoRepository;

import ru.otus.hw.models.document.BookDocument;

public interface BookDocumentRepository extends MongoRepository<BookDocument, String> {
}
