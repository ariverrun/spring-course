package ru.otus.hw.commands;

import java.util.stream.Collectors;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.services.GenreService;

@RequiredArgsConstructor
@ShellComponent
public class GenreCommands {

    private final GenreService genreService;

    private final GenreConverter genreConverter;

    @ShellMethod(value = "Find all genres", key = "ag")
    public String findAllGenres() {
        return genreService.findAll().stream()
                .map(genreConverter::genreToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find genre by id", key = "gbid")
    public String findGenreById(String id) {
        return genreService.findById(id)
                .map(genreConverter::genreToString)
                .orElse("Genre with id %s not found".formatted(id));
    }   
    
    @ShellMethod(value = "Insert genre", key = "gins")
    public String insertGenre(String name) {
        var savedGenre = genreService.insert(name);
        return genreConverter.genreToString(savedGenre);
    }

    @ShellMethod(value = "Update genre", key = "gupd")
    public String updateGenre(String id, String name) {
        var savedGenre = genreService.update(id, name);
        return genreConverter.genreToString(savedGenre);
    }

    @ShellMethod(value = "Delete genre by id", key = "gdel")
    public void deleteGenre(String id) {
        genreService.deleteById(id);
    }    
}
