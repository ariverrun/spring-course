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
import ru.otus.hw.dto.CreateGenreRequestDto;
import ru.otus.hw.dto.UpdateGenreRequestDto;
import ru.otus.hw.services.GenreService;

@Controller
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;
    
    @GetMapping("/genres")
    public String listAllGenres(Model model) {
        var genres = genreService.findAll();
        model.addAttribute("genres", genres);
        return "genres/all";
    }

    @GetMapping("/genres/{genreId}")
    public String showGenre(@PathVariable Long genreId, Model model) {
        populateModelWithGenre(genreId, model);
        return "genres/show";
    }

    @GetMapping("/genres/{genreId}/delete")
    public String showDeletePage(@PathVariable Long genreId, Model model) {
        populateModelWithGenre(genreId, model);
        return "genres/delete";
    }

    @DeleteMapping("/genres/{genreId}")
    public String deleteGenre(@PathVariable Long genreId) {
        genreService.deleteById(genreId);
        return "redirect:/genres";
    }

    @GetMapping("/genres/create")
    public String showCreatePage() {
        return "genres/create";
    }
    
    @PostMapping("/genres")
    public String createGenre(@Valid CreateGenreRequestDto requestDto) {
        genreService.insert(requestDto);
        return "redirect:/genres"; 
    }

    @GetMapping("/genres/{genreId}/edit")
    public String showEditPage(@PathVariable Long genreId, Model model) {
        populateModelWithGenre(genreId, model);     
        return "genres/edit";
    }

    @PutMapping("/genres/{genreId}")
    public String updateGenre(@PathVariable Long genreId, @Valid UpdateGenreRequestDto requestDto) {
        genreService.update(genreId, requestDto);
        return "redirect:/genres"; 
    }

    private void populateModelWithGenre(Long genreId, Model model) {
        var genre = genreService.findById(genreId);
        model.addAttribute("genre", genre);            
    }
}
