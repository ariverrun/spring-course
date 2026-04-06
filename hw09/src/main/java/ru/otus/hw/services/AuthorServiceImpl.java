package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    @Override
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    @Override
    public Optional<Author> findById(long id) {
        return authorRepository.findById(id);
    }

    @Override
    @Transactional
    public Author insert(String fullName) {
        var author = new Author(0, fullName);
        return authorRepository.save(author);
    }

    @Override
    @Transactional
    public Author update(long id, String fullName) {
        var author = authorRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(id)));
        author.setFullName(fullName);
        return authorRepository.save(author);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        authorRepository.deleteById(id);
    }

    @Override
    public Author getById(long id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(id)));
    }
}
