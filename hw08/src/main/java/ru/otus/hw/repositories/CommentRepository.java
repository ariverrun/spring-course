package ru.otus.hw.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

public interface CommentRepository extends CrudRepository<Comment, String> {
    
    List<Comment> findByBookId(String bookId);

    void deleteByBook(Book book);
}