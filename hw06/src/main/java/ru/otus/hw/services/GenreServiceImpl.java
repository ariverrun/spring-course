package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
        return save(0, name);
    }

    @Override
    @Transactional
    public Genre update(long id, String name) {
        return save(id, name);
    }    

    @Override
    @Transactional
    public void deleteById(long id) {
        genreRepository.deleteById(id);
    }

    private Genre save(long id, String name) {
        var genre = new Genre(id, name);
        return genreRepository.save(genre);
    }    
}
