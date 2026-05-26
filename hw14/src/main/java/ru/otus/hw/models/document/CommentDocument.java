package ru.otus.hw.models.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "comments")
public class CommentDocument {

    @Id
    private String id;

    private BookDocument book;

    private String text;
}
