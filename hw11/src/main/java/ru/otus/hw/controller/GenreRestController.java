package ru.otus.hw.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CreateGenreRequestDto;
import ru.otus.hw.dto.CreatedEntityDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.UpdateGenreRequestDto;
import ru.otus.hw.services.GenreService;

@RestController
@RequiredArgsConstructor
public class GenreRestController {

    private final GenreService genreService;
    
    @GetMapping("/api/v1/genre")
    public Flux<GenreDto> listAllGenres() {
        return genreService.findAll();
    }

    @GetMapping("/api/v1/genre/{genreId}")
    public Mono<GenreDto> getGenreById(@PathVariable String genreId) {
        return genreService.findById(genreId);
    }

    @DeleteMapping("/api/v1/genre/{genreId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteGenre(@PathVariable String genreId) {
        return genreService.deleteById(genreId);
    }

    @PostMapping("/api/v1/genre")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CreatedEntityDto> createGenre(@RequestBody @Valid CreateGenreRequestDto requestDto) {
        return genreService.insert(requestDto)
            .map(genre -> new CreatedEntityDto(genre.id()));
    }    

    @PutMapping("/api/v1/genre/{genreId}")
    public Mono<GenreDto> updateGenre(
        @PathVariable String genreId, 
        @RequestBody @Valid UpdateGenreRequestDto requestDto
    ) {
        return genreService.update(genreId, requestDto);
    }
}