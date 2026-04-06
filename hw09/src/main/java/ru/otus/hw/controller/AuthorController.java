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
import ru.otus.hw.requests.CreateAuthorRequestDto;
import ru.otus.hw.requests.UpdateAuthorRequestDto;
import ru.otus.hw.services.AuthorService;

@Controller
@RequiredArgsConstructor
public class AuthorController {
    
    private final AuthorService authorService;
    
    @GetMapping("/authors")
    public String listAllAuthors(Model model) {
        var authors = authorService.findAll();
        model.addAttribute("authors", authors);
        return "authors/all";
    }

    @GetMapping("/authors/{authorId}")
    public String showauthor(@PathVariable Long authorId, Model model) {
        populateModelWithAuthor(authorId, model);
        return "authors/show";
    }

    @GetMapping("/authors/{authorId}/delete")
    public String showDeletePage(@PathVariable Long authorId, Model model) {
        populateModelWithAuthor(authorId, model);
        return "authors/delete";
    }

    @DeleteMapping("/authors/{authorId}")
    public String deleteAuthor(@PathVariable Long authorId) {
        authorService.deleteById(authorId);
        return "redirect:/authors";
    }

    @GetMapping("/authors/create")
    public String showCreatePage() {
        return "authors/create";
    }
    
    @PostMapping("/authors")
    public String createAuthor(@Valid CreateAuthorRequestDto requestDto) {
        authorService.insert(requestDto.fullName());
        return "redirect:/authors"; 
    }

    @GetMapping("/authors/{authorId}/edit")
    public String showEditPage(@PathVariable Long authorId, Model model) {
        populateModelWithAuthor(authorId, model);     
        return "authors/edit";
    }

    @PutMapping("/authors/{authorId}")
    public String updateAuthor(@PathVariable Long authorId, @Valid UpdateAuthorRequestDto requestDto) {
        authorService.update(authorId, requestDto.fullName());
        return "redirect:/authors"; 
    }

    private void populateModelWithAuthor(Long authorId, Model model) {
        var author = authorService.getById(authorId);
        model.addAttribute("author", author);            
    }    
}
