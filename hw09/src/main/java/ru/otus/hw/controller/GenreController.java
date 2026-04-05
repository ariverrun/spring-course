package ru.otus.hw.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.models.Genre;
import ru.otus.hw.requests.CreateGenreRequestDto;
import ru.otus.hw.requests.UpdateGenreRequestDto;
import ru.otus.hw.services.GenreService;

@Controller
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;
    
    @GetMapping("/genres")
    public String listAllGenres(Model model) {
        List<Genre> genres = genreService.findAll();
        model.addAttribute("genres", genres);
        return "genres/all";
    }

    @GetMapping("/genres/{genreId}")
    public String showGenre(@PathVariable Long genreId, Model model) {
        var genre = genreService.getById(genreId);
        model.addAttribute("genre", genre);
        return "genres/show";
    }

    @GetMapping("/genres/{genreId}/delete")
    public String showDeletePage(@PathVariable Long genreId, Model model) {
        var genre = genreService.getById(genreId);
        model.addAttribute("genre", genre);
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
    public String createGenre(CreateGenreRequestDto requestDto) {
        genreService.insert(requestDto.name());
        return "redirect:/genres"; 
    }

    @GetMapping("/genres/{genreId}/edit")
    public String showEditPage(@PathVariable Long genreId, Model model) {
        var genre = genreService.getById(genreId);
        model.addAttribute("genre", genre);        
        return "genres/edit";
    }

    @PutMapping("/genres/{genreId}")
    public String updateGenre(@PathVariable Long genreId, UpdateGenreRequestDto requestDto) {
        genreService.update(genreId, requestDto.name());
        return "redirect:/genres"; 
    }    
}
