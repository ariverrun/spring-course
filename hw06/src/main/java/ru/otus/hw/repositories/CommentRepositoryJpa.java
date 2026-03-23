package ru.otus.hw.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.models.Comment;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryJpa implements CommentRepository {
    @PersistenceContext
    private EntityManager entityManager;

    private final BookRepository bookRepository;

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            entityManager.persist(comment);
            return comment;
        } else {
            return entityManager.merge(comment);
        }    
    }

    @Override
    public List<Comment> findByBookId(Long bookId) {
        if (bookId == null) {
            return List.of();
        }

        var optionalBook = bookRepository.findById(bookId);

        if (optionalBook.isEmpty()) {
            return List.of();
        }

        var comments = entityManager.createQuery(
            "SELECT c FROM Comment c WHERE c.book = :book", 
            Comment.class)
            .setParameter("book", optionalBook.get())
            .getResultList();

        return comments;
    }

    @Override
    public Optional<Comment> findById(long id) {
        return Optional.ofNullable(entityManager.find(Comment.class, id));
    }

    @Override
    public void deleteById(long id) {
        var comment = findById(id);
        if (comment.isPresent()) {
            entityManager.remove(comment.get());
        }
    }
}
