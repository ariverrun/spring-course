package ru.otus.hw.repositories.document;

import org.springframework.data.mongodb.repository.MongoRepository;

import ru.otus.hw.models.document.AuthorDocument;

public interface AuthorDocumentRepository extends MongoRepository<AuthorDocument, String> {
}
