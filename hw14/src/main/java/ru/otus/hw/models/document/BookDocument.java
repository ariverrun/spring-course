package ru.otus.hw.models.document;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "books")
public class BookDocument {

    @Id
    private String id;

    private String title;

    private AuthorDocument author;

    private List<GenreDocument> genres = new ArrayList<>();
}
