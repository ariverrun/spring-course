package ru.otus.hw.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ru.otus.hw.models.Comment;

@Repository
public class CommentRepositoryJpa implements CommentRepository {
    @PersistenceContext
    private EntityManager entityManager;    

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
    public List<Comment> findAll() {
        return entityManager.createQuery("SELECT c FROM Comment c", Comment.class)
                .getResultList();
    }

    @Override
    public List<Comment> findByBookId(Long bookId) {
        if (bookId == null) {
            return List.of();
        }
        
        return entityManager.createQuery(
            "SELECT c FROM Comment c WHERE c.bookId = :bookId", 
            Comment.class)
            .setParameter("bookId", bookId)
            .getResultList();
    }
}
