package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    public Optional<Genre> findById(long id) {
        return genreRepository.findById(id);
    } 
    
    @Override
    @Transactional
    public Genre insert(String name) {
        var genre = new Genre(0, name);
        return genreRepository.save(genre);
    }

    @Override
    @Transactional
    public Genre update(long id, String name) {
        var genre = genreRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Genre with id %d not found".formatted(id)));
        genre.setName(name);
        return genreRepository.save(genre);
    }    

    @Override
    @Transactional
    public void deleteById(long id) {
        genreRepository.deleteById(id);
    }

    @Override
    public Genre getById(long id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException("Genre with id %d not found".formatted(id)));
    }
}
