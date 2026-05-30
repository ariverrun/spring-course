package ru.otus.hw.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CreateCommentDto;
import ru.otus.hw.dto.UpdateCommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.document.BookDocument;
import ru.otus.hw.models.document.CommentDocument;
import ru.otus.hw.repositories.document.BookDocumentRepository;
import ru.otus.hw.repositories.document.CommentDocumentRepository;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final BookDocumentRepository bookRepository;

    private final CommentDocumentRepository commentRepository;

    @Override
    public CommentDto insert(CreateCommentDto dto) {
        BookDocument book = getBookById(dto.bookId());
        return mapToDto(commentRepository.save(new CommentDocument(null, book, dto.text())));
    }

    @Override
    public CommentDto update(UpdateCommentDto dto) {
        CommentDocument comment = commentRepository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Comment with id %s not found".formatted(dto.id())));
        comment.setText(dto.text());
        return mapToDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> findByBookId(String bookId) {
        return commentRepository.findByBookId(bookId).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public CommentDto findById(String id) {
        return commentRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Comment with id %s not found".formatted(id)));
    }

    @Override
    public void deleteById(String id) {
        commentRepository.deleteById(id);
    }

    private BookDocument getBookById(String bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Book with id %s not found".formatted(bookId)));
    }

    private CommentDto mapToDto(CommentDocument comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getBook().getId(),
                comment.getBook().getTitle()
        );
    }
}
