package ru.otus.hw.services;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.dto.CreateGenreRequestDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.UpdateGenreRequestDto;
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
    public GenreDto findById(long id) {
        var genre = genreRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Genre with id %d not found".formatted(id)));
        return new GenreDto(id, genre.getName());
    } 
    
    @Override
    @Transactional
    public Genre insert(CreateGenreRequestDto dto) {
        var genre = new Genre(0, dto.name());
        return genreRepository.save(genre);
    }

    @Override
    @Transactional
    public Genre update(long id, UpdateGenreRequestDto dto) {
        var genre = genreRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Genre with id %d not found".formatted(id)));
        genre.setName(dto.name());
        return genreRepository.save(genre);
    }    

    @Override
    @Transactional
    public void deleteById(long id) {
        genreRepository.deleteById(id);
    }
}
