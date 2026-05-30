package ru.otus.hw.repositories.document;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import ru.otus.hw.models.document.CommentDocument;

public interface CommentDocumentRepository extends MongoRepository<CommentDocument, String> {

    List<CommentDocument> findByBookId(String bookId);

    void deleteByBookId(String bookId);
}
