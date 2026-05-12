package ru.otus.hw.services;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CreateCommentDto;
import ru.otus.hw.dto.UpdateCommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;
    
    private final AclServiceWrapperService aclServiceWrapperService;

    @Override
    @Transactional
    public CommentDto insert(CreateCommentDto dto) {
        var comment = new Comment(0, getBookById(dto.bookId()), dto.text());
        comment = commentRepository.save(comment);
        
        aclServiceWrapperService.createPermission(comment, BasePermission.READ);
        aclServiceWrapperService.createPermission(comment, BasePermission.WRITE);
        aclServiceWrapperService.createPermission(comment, BasePermission.DELETE);

        return commentMapper.mapCommentToDto(comment);
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#dto.id, 'ru.otus.hw.models.Comment', 'WRITE')")
    public CommentDto update(UpdateCommentDto dto) {
        var comment = commentRepository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %d not found".formatted(dto.id())));
        comment.setText(dto.text());
        return commentMapper.mapCommentToDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findByBookId(Long bookId) {
        return commentRepository.findByBookId(bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto findById(long id) {
        var comment = commentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Comment with id %d not found".formatted(id)));
        return commentMapper.mapCommentToDto(comment);
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'ru.otus.hw.models.Comment', 'DELETE')")
    public void deleteById(long id) {
        var comment = commentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Comment with id %d not found".formatted(id)));
        commentRepository.deleteById(id);
        aclServiceWrapperService.deleteAcl(comment);
    }

    private Book getBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));
    } 
}
