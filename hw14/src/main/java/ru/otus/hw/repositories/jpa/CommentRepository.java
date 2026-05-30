package ru.otus.hw.repositories.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.otus.hw.models.jpa.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByBookId(Long bookId);

    void deleteByBookId(Long bookId);

    @Query("SELECT c FROM Comment c JOIN FETCH c.book")
    List<Comment> findAllWithBook();
}
