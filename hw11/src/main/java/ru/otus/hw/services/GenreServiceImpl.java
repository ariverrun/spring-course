package ru.otus.hw.services;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CreateGenreRequestDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.UpdateGenreRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.GenreMapper;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final GenreMapper genreMapper;

    @Override
    public Flux<GenreDto> findAll() {
        return genreRepository.findAll()
            .map(genreMapper::mapGenreToDto);
    }

    @Override
    public Mono<GenreDto> findById(String id) {
        return genreRepository.findById(id)
            .map(genreMapper::mapGenreToDto)
            .switchIfEmpty(Mono.error(new EntityNotFoundException("Genre with id %s not found".formatted(id))));
    } 
    
    @Override
    public Mono<GenreDto> insert(CreateGenreRequestDto dto) {
        var genre = new Genre(null, dto.name());
        return genreRepository.save(genre)
            .map(genreMapper::mapGenreToDto);
    }

    @Override
    public Mono<GenreDto> update(String id, UpdateGenreRequestDto dto) {
        return genreRepository.findById(id)
            .switchIfEmpty(Mono.error(new EntityNotFoundException("Genre with id %s not found".formatted(id))))
            .flatMap(genre -> {
                genre.setName(dto.name());
                return genreRepository.save(genre);
            })
            .map(genreMapper::mapGenreToDto);
    }    

    @Override
    public Mono<Void> deleteById(String id) {
        return bookRepository.findBooksByGenreId(id)
            .flatMap(book -> {
                book.getGenres().removeIf(genre -> genre.getId().equals(id));
                return bookRepository.save(book);
            })
            .then(genreRepository.deleteById(id));
    }
}