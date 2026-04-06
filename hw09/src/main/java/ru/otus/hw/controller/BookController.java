package ru.otus.hw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.requests.CreateBookRequestDto;
import ru.otus.hw.requests.UpdateBookRequestDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    private final CommentService commentService;
    
    @GetMapping({"/books", "/"})
    public String listAllBooks(Model model) {
        var books = bookService.findAll();
        model.addAttribute("books", books);
        return "books/all";
    }

    @GetMapping("/books/{bookId}")
    public String showBook(@PathVariable Long bookId, Model model) {
        populateModelWithBook(bookId, model);
        populateModelWithBookComments(bookId, model);
        return "books/show";
    }
    
    @GetMapping("/books/{bookId}/delete")
    public String showDeletePage(@PathVariable Long bookId, Model model) {
        populateModelWithBook(bookId, model);
        return "books/delete";
    }

    @DeleteMapping("/books/{bookId}")
    public String deleteBook(@PathVariable Long bookId) {
        bookService.deleteById(bookId);
        return "redirect:/books";
    }

    @GetMapping("/books/create")
    public String showCreatePage(Model model) {
        populateModelWithBookFormAttrs(model);
        return "books/create";
    }

    @PostMapping("/books")
    public String createBook(@Valid CreateBookRequestDto requestDto) {
        bookService.insert(requestDto.title(), requestDto.authorId(), requestDto.genreIds());
        return "redirect:/books"; 
    }    

    @GetMapping("/books/{bookId}/edit")
    public String showEditPage(@PathVariable Long bookId, Model model) {
        populateModelWithBook(bookId, model);
        populateModelWithBookFormAttrs(model);
        return "books/edit";
    }

    @PutMapping("/books/{bookId}")
    public String updateBook(@PathVariable Long bookId, @Valid UpdateBookRequestDto requestDto) {
        bookService.update(bookId, requestDto.title(), requestDto.authorId(), requestDto.genreIds());
        return "redirect:/books"; 
    }

    private void populateModelWithBook(Long bookId, Model model) {
        var book = bookService.getById(bookId);
        model.addAttribute("book", book);        
    }

    private void populateModelWithBookFormAttrs(Model model) {
        var authors = authorService.findAll();
        model.addAttribute("authors", authors);
        var genres = genreService.findAll();
        model.addAttribute("genres", genres);
    }

    private void populateModelWithBookComments(Long bookId, Model model) {
        var comments = commentService.findByBookId(bookId);
        model.addAttribute("comments", comments);
    }
}
