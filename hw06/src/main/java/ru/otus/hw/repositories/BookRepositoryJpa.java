package ru.otus.hw.repositories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import ru.otus.hw.models.Book;

@Repository
public class BookRepositoryJpa implements BookRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Book> findById(long id) {
        EntityGraph<?> entityGraph = entityManager.getEntityGraph("book-author-and-genres-entity-graph");
        Map<String, Object> hints = new HashMap<>();
        hints.put("jakarta.persistence.fetchgraph", entityGraph);
        Book book = entityManager.find(Book.class, id, hints);
        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> findAll() {
        EntityGraph<?> entityGraph = entityManager.getEntityGraph("book-author-entity-graph");
        TypedQuery<Book> query = entityManager.createQuery("SELECT b FROM Book b", Book.class);
        query.setHint("javax.persistence.fetchgraph", entityGraph);
        var books = query.getResultList();
        initBooksLazyProperities(books);
        return books;
    }

    @Override
    public Book save(Book book) {
        return entityManager.merge(book);
    }

    @Override
    public void deleteById(long id) {
        var book = findById(id);
        if (book.isPresent()) {
            entityManager.remove(book.get());
        }
    }

    @Override
    public boolean existsById(long id) {
        if (findById(id).isPresent()) {
            return true;
        }

        return false;
    }

    private void initBooksLazyProperities(List<Book> books) {
        books.forEach(book -> book.getGenres().size());
    }
}
