package ru.otus.hw.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dto.CreateGenreRequestDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.UpdateGenreRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.document.GenreDocument;
import ru.otus.hw.repositories.document.GenreDocumentRepository;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private final GenreDocumentRepository genreRepository;

    @Override
    public List<GenreDto> findAll() {
        return genreRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public GenreDto findById(String id) {
        return genreRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id %s not found".formatted(id)));
    }

    @Override
    public GenreDto insert(CreateGenreRequestDto dto) {
        return mapToDto(genreRepository.save(new GenreDocument(null, dto.name())));
    }

    @Override
    public GenreDto update(String id, UpdateGenreRequestDto dto) {
        GenreDocument genre = genreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id %s not found".formatted(id)));
        genre.setName(dto.name());
        return mapToDto(genreRepository.save(genre));
    }

    @Override
    public void deleteById(String id) {
        genreRepository.deleteById(id);
    }

    private GenreDto mapToDto(GenreDocument genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }
}
