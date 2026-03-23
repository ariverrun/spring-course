package ru.otus.hw.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.otus.hw.models.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    
    @Override
    @EntityGraph(attributePaths = {"author", "genres"})
    Optional<Book> findById(Long id);
    
    @Override
    @EntityGraph(attributePaths = {"author"})
    List<Book> findAll();
}