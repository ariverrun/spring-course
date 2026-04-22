package ru.otus.hw.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CreateCommentDto;
import ru.otus.hw.dto.UpdateCommentDto;
import ru.otus.hw.dto.UpdateCommentRequestDto;
import ru.otus.hw.services.CommentService;

@RestController
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;
    
    @GetMapping("/api/v1/comment")
    public Flux<CommentDto> getCommentsByBookId(@RequestParam Long bookId) {
        return commentService.findByBookId(bookId);
    }

    @GetMapping("/api/v1/comment/{commentId}")
    public Mono<CommentDto> getCommentById(@PathVariable Long commentId) {
        return commentService.findById(commentId);
    }

    @DeleteMapping("/api/v1/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteComment(@PathVariable Long commentId) {
        return commentService.deleteById(commentId);
    }

    @PostMapping("/api/v1/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CommentDto> createComment(@RequestBody @Valid CreateCommentDto requestDto) {
        return commentService.insert(requestDto);
    }    

    @PutMapping("/api/v1/comment/{commentId}")
    public Mono<CommentDto> updateComment(
        @PathVariable Long commentId, 
        @RequestBody @Valid UpdateCommentRequestDto requestDto
    ) {
        return commentService.update(new UpdateCommentDto(commentId, requestDto.text()));
    }
}