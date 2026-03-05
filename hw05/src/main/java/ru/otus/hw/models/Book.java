package ru.otus.hw.models;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    private long id;

    private String title;

    private Author author;

    private List<Genre> genres;

    public void addGenre(Genre genre) {
        if (genres == null) {
            genres = new ArrayList<>();
        }
        genres.add(genre);
    }
}
