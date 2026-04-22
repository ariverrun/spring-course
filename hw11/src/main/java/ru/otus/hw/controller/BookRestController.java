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
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CreateBookRequestDto;
import ru.otus.hw.dto.CreatedEntityDto;
import ru.otus.hw.dto.UpdateBookRequestDto;
import ru.otus.hw.services.BookService;

@RestController
@RequiredArgsConstructor
public class BookRestController {

    private final BookService bookService;
    
    @GetMapping({"/api/v1/book"})
    public List<BookDto> listAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/api/v1/book/{bookId}")
    public BookDto getBookById(@PathVariable Long bookId) {
        return bookService.findById(bookId);
    }

    @DeleteMapping("/api/v1/book/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long bookId) {
        bookService.deleteById(bookId);
    }

    @PostMapping("/api/v1/book")
    @ResponseStatus(HttpStatus.CREATED)
    public CreatedEntityDto createBook(@RequestBody @Valid CreateBookRequestDto requestDto) {
        var book = bookService.insert(requestDto);
        return new CreatedEntityDto(book.id());
    }    

    @PutMapping("/api/v1/book/{bookId}")
    public BookDto updateBook(@PathVariable Long bookId, @RequestBody @Valid UpdateBookRequestDto requestDto) {
        return bookService.update(bookId, requestDto);
    }
}
