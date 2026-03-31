package ru.otus.hw.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "books")
public class Book {
    
    @Id    
    private String id;

    private String title;

    @DBRef
    private Author author;

    @DBRef
    private List<Genre> genres = new ArrayList<>();

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void removeGenres() {
        genres = new ArrayList<>();
    }

    public void removeGenreById(String genreId) {
        genres.removeIf(genre -> genre != null && genre.getId().equals(genreId));
    }
}