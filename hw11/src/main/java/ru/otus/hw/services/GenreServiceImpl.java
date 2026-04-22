package ru.otus.hw.services;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
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
    public Flux<GenreDto> findAll() {
        return Mono.fromCallable(genreRepository::findAll)
            .flatMapMany(Flux::fromIterable)
            .map(this::mapGenreToDto)
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<GenreDto> findById(long id) {
        return Mono.fromCallable(() -> genreRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Genre with id %d not found".formatted(id))))
            .map(this::mapGenreToDto)
            .subscribeOn(Schedulers.boundedElastic());
    } 
    
    @Override
    @Transactional
    public Mono<GenreDto> insert(CreateGenreRequestDto dto) {
        return Mono.fromCallable(() -> {
            var genre = new Genre(0, dto.name());
            return genreRepository.save(genre);
        })
        .map(this::mapGenreToDto)
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<GenreDto> update(long id, UpdateGenreRequestDto dto) {
        return Mono.fromCallable(() -> {
            var genre = genreRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Genre with id %d not found".formatted(id)));
            genre.setName(dto.name());
            return genreRepository.save(genre);
        })
        .map(this::mapGenreToDto)
        .subscribeOn(Schedulers.boundedElastic());
    }    

    @Override
    @Transactional
    public Mono<Void> deleteById(long id) {
        return Mono.fromRunnable(() -> genreRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private GenreDto mapGenreToDto(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }
}
