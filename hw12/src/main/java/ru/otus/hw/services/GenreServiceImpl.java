package ru.otus.hw.services;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.dto.CreateGenreRequestDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.UpdateGenreRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.GenreMapper;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    private final GenreMapper genreMapper;

    @Override
    public List<GenreDto> findAll() {
        return genreRepository.findAll().stream()
            .map(g -> genreMapper.mapGenreToDto(g))
            .toList();
    }

    @Override
    public GenreDto findById(long id) {
        var genre = genreRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Genre with id %d not found".formatted(id)));
        return genreMapper.mapGenreToDto(genre);
    } 
    
    @Override
    @Transactional
    public GenreDto insert(CreateGenreRequestDto dto) {
        var genre = new Genre(0, dto.name());
        return genreMapper.mapGenreToDto(genreRepository.save(genre));
    }

    @Override
    @Transactional
    public GenreDto update(long id, UpdateGenreRequestDto dto) {
        var genre = genreRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Genre with id %d not found".formatted(id)));
        genre.setName(dto.name());
        return genreMapper.mapGenreToDto(genreRepository.save(genre));
    }    

    @Override
    @Transactional
    public void deleteById(long id) {
        genreRepository.deleteById(id);
    }
}
