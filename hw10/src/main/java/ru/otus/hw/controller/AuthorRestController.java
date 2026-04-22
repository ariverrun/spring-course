package ru.otus.hw.controller;

import java.util.List;

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
    public List<AuthorDto> listAllAuthors() {
        return authorService.findAll();
    }

    @GetMapping("/api/v1/author/{authorId}")
    public AuthorDto getAuthorById(@PathVariable Long authorId) {
        return authorService.findById(authorId);
    }

    @DeleteMapping("/api/v1/author/{authorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuthor(@PathVariable Long authorId) {
        authorService.deleteById(authorId);
    }

    @PostMapping("/api/v1/author")
    @ResponseStatus(HttpStatus.CREATED)
    public CreatedEntityDto createAuthor(@RequestBody @Valid CreateAuthorRequestDto requestDto) {
        var author = authorService.insert(requestDto);
        return new CreatedEntityDto(author.id());
    }

    @PutMapping("/api/v1/author/{authorId}")
    public AuthorDto updateAuthor(@PathVariable Long authorId, @RequestBody @Valid UpdateAuthorRequestDto requestDto) {
        return authorService.update(authorId, requestDto);
    }
}