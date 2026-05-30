package ru.otus.hw.repositories.document;

import org.springframework.data.mongodb.repository.MongoRepository;

import ru.otus.hw.models.document.GenreDocument;

public interface GenreDocumentRepository extends MongoRepository<GenreDocument, String> {
}
