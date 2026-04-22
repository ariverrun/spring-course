package ru.otus.hw.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.otus.hw.models.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    @EntityGraph(attributePaths = {"book"})
    List<Comment> findByBookId(Long bookId);

    void deleteByBookId(Long bookId);
}