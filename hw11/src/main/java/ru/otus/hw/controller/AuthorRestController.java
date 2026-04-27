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
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.CreateAuthorRequestDto;
import ru.otus.hw.dto.CreatedEntityDto;
import ru.otus.hw.dto.UpdateAuthorRequestDto;
import ru.otus.hw.services.AuthorService;

@RestController
@RequiredArgsConstructor
public class AuthorRestController {

    private final AuthorService authorService;
    
    @GetMapping("/api/v1/author")
    public Flux<AuthorDto> listAllAuthors() {
        return authorService.findAll();
    }

    @GetMapping("/api/v1/author/{authorId}")
    public Mono<AuthorDto> getAuthorById(@PathVariable String authorId) {
        return authorService.findById(authorId);
    }

    @DeleteMapping("/api/v1/author/{authorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteAuthor(@PathVariable String authorId) {
        return authorService.deleteById(authorId);
    }

    @PostMapping("/api/v1/author")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CreatedEntityDto> createAuthor(@RequestBody @Valid CreateAuthorRequestDto requestDto) {
        return authorService.insert(requestDto)
                .map(author -> new CreatedEntityDto(author.id()));
    }

    @PutMapping("/api/v1/author/{authorId}")
    public Mono<AuthorDto> updateAuthor(
        @PathVariable String authorId, 
        @RequestBody @Valid UpdateAuthorRequestDto requestDto
    ) {
        return authorService.update(authorId, requestDto);
    }
}