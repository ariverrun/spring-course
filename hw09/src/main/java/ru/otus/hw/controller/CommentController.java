package ru.otus.hw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.requests.CreateCommentRequestDto;
import ru.otus.hw.requests.UpdateCommentRequestDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

@Controller
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;

    private final BookService bookService;

    @GetMapping("/comments/{commentId}/delete")
    public String showDeletePage(@PathVariable Long commentId, Model model) {
        populateModelWithCommentAndBook(commentId, model);
        return "comments/delete";
    }

    @DeleteMapping("/comments/{commentId}")
    public String deleteComment(@PathVariable Long commentId) {
        var comment = commentService.getById(commentId);
        var book = comment.getBook();
        commentService.deleteById(commentId);
        return "redirect:/books/" + book.getId();
    }

    @GetMapping("/comments/{commentId}")
    public String showComment(@PathVariable Long commentId, Model model) {
        populateModelWithCommentAndBook(commentId, model);
        return "comments/show";
    }

    @GetMapping("/comments/{commentId}/edit")
    public String showEditPage(@PathVariable Long commentId, Model model) {
        populateModelWithCommentAndBook(commentId, model);
        return "comments/edit";
    }

    @PutMapping("/comments/{commentId}")
    public String updateComment(@PathVariable Long commentId, @Valid UpdateCommentRequestDto requestDto) {
        var comment = commentService.getById(commentId);
        var book = comment.getBook();
        commentService.update(commentId, requestDto.text(), book.getId());
        return "redirect:/books/" + book.getId();
    }

    @GetMapping("/books/{bookId}/comments/create")
    public String showCreatePage(@PathVariable Long bookId, Model model) {
        populateModelWithBook(bookId, model);
        return "comments/create";
    }
    
    @PostMapping("/books/{bookId}/comments")
    public String createComment(@PathVariable Long bookId, @Valid CreateCommentRequestDto requestDto) {
        commentService.insert(requestDto.text(), bookId);
        return "redirect:/books/" + bookId;
    }

    private void populateModelWithCommentAndBook(Long commentId, Model model) {
        var comment = commentService.getById(commentId);
        model.addAttribute("comment", comment);
        var book = comment.getBook();
        model.addAttribute("book", book);
    }

    private void populateModelWithBook(Long bookId, Model model) {
        var book = bookService.getById(bookId);
        model.addAttribute("book", book);        
    }    
}
