package ru.otus.hw.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private CommentService commentService;

    @Test
    void shouldShowAllBooks() throws Exception {
        var expectedBooks = getDbBooks();
        
        when(bookService.findAll()).thenReturn(expectedBooks);
        
        mockMvc.perform(get("/books"))
            .andExpect(status().isOk())
            .andExpect(view().name("books/all"))
            .andExpect(model().attributeExists("books"))
            .andExpect(model().attribute("books", expectedBooks));
        
        verify(bookService).findAll();
    }

    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldShowOneBook(Book expectedBook) throws Exception {        
        when(bookService.getById(expectedBook.getId())).thenReturn(expectedBook);
        var expectedComments = getDbCommentsByBookId(expectedBook.getId());
        when(commentService.findByBookId(expectedBook.getId())).thenReturn(expectedComments);
        
        mockMvc.perform(get("/books/{id}", expectedBook.getId()))
            .andExpect(status().isOk())
            .andExpect(view().name("books/show"))
            .andExpect(model().attributeExists("book"))
            .andExpect(model().attribute("book", expectedBook))
            .andExpect(model().attributeExists("comments"))
            .andExpect(model().attribute("comments", expectedComments));
        
        verify(bookService).getById(expectedBook.getId());
    }

    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldShowDeletePage(Book expectedBook) throws Exception {        
        when(bookService.getById(expectedBook.getId())).thenReturn(expectedBook);
        
        mockMvc.perform(get("/books/{id}/delete", expectedBook.getId()))
            .andExpect(status().isOk())
            .andExpect(view().name("books/delete"))
            .andExpect(model().attributeExists("book"))
            .andExpect(model().attribute("book", expectedBook));
        
        verify(bookService).getById(expectedBook.getId());
    }

    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldDeleteBook(Book expectedBook) throws Exception {               
        mockMvc.perform(delete("/books/{bookId}", expectedBook.getId()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/books"));
        
        verify(bookService).deleteById(expectedBook.getId());
    }

    @Test
    void shouldShowСreatePage() throws Exception {
        var expectedAuthors = getDbAuthors();
        when(authorService.findAll()).thenReturn(expectedAuthors);

        var expectedGenres = getDbGenres();
        when(genreService.findAll()).thenReturn(expectedGenres);

        mockMvc.perform(get("/books/create"))
            .andExpect(status().isOk())
            .andExpect(view().name("books/create"))
            .andExpect(model().attributeExists("authors"))
            .andExpect(model().attribute("authors", expectedAuthors))
            .andExpect(model().attributeExists("genres"))
            .andExpect(model().attribute("genres", expectedGenres));            
        
        verify(authorService).findAll();
    }

    private static List<Book> getDbBooks() {
        Author author1 = new Author(1L, "Author_1");
        Author author2 = new Author(2L, "Author_2");
        Author author3 = new Author(3L, "Author_3");
        
        Genre genre1 = new Genre(1L, "Genre_1");
        Genre genre2 = new Genre(2L, "Genre_2");
        Genre genre3 = new Genre(3L, "Genre_3");
        Genre genre4 = new Genre(4L, "Genre_4");
        Genre genre5 = new Genre(5L, "Genre_5");
        Genre genre6 = new Genre(6L, "Genre_6");
        
        Book book1 = new Book(1L, "BookTitle_1", author1, new ArrayList<>());
        book1.addGenre(genre1);
        book1.addGenre(genre2);
        
        Book book2 = new Book(2L, "BookTitle_2", author2, new ArrayList<>());
        book2.addGenre(genre3);
        book2.addGenre(genre4);
        
        Book book3 = new Book(3L, "BookTitle_3", author3, new ArrayList<>());
        book3.addGenre(genre5);
        book3.addGenre(genre6);
        
        return List.of(book1, book2, book3);
    }

    private static List<Comment> getDbCommentsByBookId(long bookId) {
        return switch ((int) bookId) {
            case 1 -> List.of(
                new Comment(1L, new Book(1L, "BookTitle_1", null, null), "Comment_1"),
                new Comment(2L, new Book(1L, "BookTitle_1", null, null), "Comment_2")
            );
            case 2 -> List.of(
                new Comment(3L, new Book(2L, "BookTitle_2", null, null), "Comment_3")
            );
            case 3 -> List.of(
                new Comment(4L, new Book(3L, "BookTitle_3", null, null), "Comment_4")
            );
            default -> List.of();
        };
    }
    
    private static List<Author> getDbAuthors() {
        return List.of(
            new Author(1L, "Author_1"),
            new Author(2L, "Author_2"),
            new Author(3L, "Author_3")
        );
    }

    private static List<Genre> getDbGenres() {
        return List.of(
            new Genre(1L, "Genre_1"),
            new Genre(2L, "Genre_2"),
            new Genre(3L, "Genre_3"),
            new Genre(4L, "Genre_4"),
            new Genre(5L, "Genre_5"),
            new Genre(6L, "Genre_6")
        );
    }    
}