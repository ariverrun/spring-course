package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;
    
    @Override
    @Transactional
    public Comment insert(String text, Long bookId) {
        return save(0, text, bookId);
    }

    @Override
    @Transactional
    public Comment update(long id, String text, Long bookId) {
        return save(id, text, bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findByBookId(Long bookId) {
        var comments = commentRepository.findByBookId(bookId);
        initCommentsLazyProperities(comments);
        return comments;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> findById(long id) {
        var optionalComment = commentRepository.findById(id);
        if (optionalComment.isPresent()) {
            bookRepository.findById(optionalComment.get().getBook().getId());
            initCommentLazyProperities(optionalComment.get());
        }
        return optionalComment;
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }

    private Comment save(long id, String text, Long bookId) {
        var optionalBook = bookRepository.findById(bookId);

        if (optionalBook.isEmpty()) {
            throw new EntityNotFoundException("Book with id %d not found".formatted(bookId));
        }

        var comment = new Comment();
        comment.setId(id);
        comment.setText(text);
        comment.setBook(optionalBook.get());

        return commentRepository.save(comment);
    }
    
    private void initCommentsLazyProperities(List<Comment> comments) {
        comments.forEach(comment -> comment.getBook());
    }

    private void initCommentLazyProperities(Comment comment) {
        comment.getBook().getAuthor().getFullName();
        comment.getBook().getGenres().size();
    }    
}
