package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

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
        return Streamable.of(genreRepository.findAll()).toList();
    }

    @Override
    public Optional<Genre> findById(String id) {
        return genreRepository.findById(id);
    } 
    
    @Override
    public Genre insert(String name) {
        var genre = new Genre(null, name);
        return genreRepository.save(genre);
    }

    @Override
    public Genre update(String id, String name) {
        var genre = genreRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Genre with id %s not found".formatted(id)));
        genre.setName(name);
        return genreRepository.save(genre);
    }    

    @Override
    public void deleteById(String id) {
        genreRepository.deleteById(id);
    }
}
