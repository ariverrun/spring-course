package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    private final BookRepository bookRepository;

    @Override
    public List<Author> findAll() {
        return Streamable.of(authorRepository.findAll()).toList();
    }

    @Override
    public Optional<Author> findById(String id) {
        return authorRepository.findById(id);
    }

    @Override
    public Author insert(String fullName) {
        var author = new Author(null, fullName);
        return authorRepository.save(author);
    }

    @Override
    public Author update(String id, String fullName) {
        var author = authorRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(id)));
        author.setFullName(fullName);
        return authorRepository.save(author);
    }

    @Override
    public void deleteById(String id) {
        var author = authorRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(id)));
        bookRepository.deleteByAuthor(author);
        authorRepository.deleteById(id);
    }
}
