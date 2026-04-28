package ru.otus.hw.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.otus.hw.config.SecurityConfig;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CreateBookRequestDto;
import ru.otus.hw.dto.CreatedEntityDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.UpdateBookRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.BookService;

@WebMvcTest(BookRestController.class)
@Import(SecurityConfig.class)
class BookRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @Test
    @SuppressWarnings("null")
    @WithMockUser
    void shouldListAllBooks() throws Exception {
        var expectedResult = getDbBookDtos();
        
        when(bookService.findAll()).thenReturn(expectedResult);
        
        mockMvc.perform(get("/api/v1/book"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));
        
        verify(bookService).findAll();
    }

    @Test
    @SuppressWarnings("null")
    void shouldNotListAllBooksForAnon() throws Exception {                
        mockMvc.perform(get("/api/v1/book"))
            .andExpect(status().isUnauthorized())
            .andExpect(content().json(objectMapper.writeValueAsString(getUnauthorizedResponseData())));        
    }

    @ParameterizedTest
    @MethodSource("getDbBookDtos")
    @SuppressWarnings("null")
    @WithMockUser
    void shouldGetBookById(BookDto expectedResult) throws Exception {
        when(bookService.findById(expectedResult.id())).thenReturn(expectedResult);
        
        mockMvc.perform(get("/api/v1/book/{bookId}", expectedResult.id()))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));
        
        verify(bookService).findById(expectedResult.id());
    }

    @Test
    @SuppressWarnings("null")
    void shouldNotGetBookByIdForAnon() throws Exception {        
        mockMvc.perform(get("/api/v1/book/{bookId}", 1L))
            .andExpect(status().isUnauthorized())
            .andExpect(content().json(objectMapper.writeValueAsString(getUnauthorizedResponseData())));        
    }
 
    @ParameterizedTest
    @MethodSource("getDbBookDtos")
    @SuppressWarnings("null")
    @WithMockUser
    void shouldDeleteBook(BookDto expectedResult) throws Exception {
        mockMvc.perform(delete("/api/v1/book/{bookId}", expectedResult.id())
            .with(csrf()))
            .andExpect(status().isNoContent());
        
        verify(bookService).deleteById(expectedResult.id());
    }

    @Test
    @SuppressWarnings("null")
    void shouldNotDeleteBookForAnon() throws Exception {
        mockMvc.perform(delete("/api/v1/book/{bookId}", 1L)
            .with(csrf()))
            .andExpect(status().isUnauthorized())
            .andExpect(content().json(objectMapper.writeValueAsString(getUnauthorizedResponseData())));        
    }

    @ParameterizedTest
    @MethodSource("getCreateBookTestData")
    @SuppressWarnings("null")
    @WithMockUser
    void shouldCreateBook(CreateBookRequestDto requestDto, BookDto expectedBook) throws Exception {
        when(bookService.insert(requestDto)).thenReturn(expectedBook);

        mockMvc.perform(post("/api/v1/book")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(
                new CreatedEntityDto(expectedBook.id())
            )));
    
        verify(bookService).insert(requestDto);
    }

    @Test
    @SuppressWarnings("null")
    void shouldNotCreateBookForAnon() throws Exception {
        mockMvc.perform(post("/api/v1/book")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new CreateBookRequestDto("New Book 1", 1L, Set.of(1L, 2L))
            )))
            .andExpect(status().isUnauthorized())
            .andExpect(content().json(objectMapper.writeValueAsString(getUnauthorizedResponseData())));    
    }

    @ParameterizedTest
    @MethodSource("getUpdateBookTestData")
    @SuppressWarnings("null")
    @WithMockUser
    void shouldUpdateBook(UpdateBookRequestDto requestDto, Long bookId, BookDto expectedBook) throws Exception {
        when(bookService.update(bookId, requestDto)).thenReturn(expectedBook);
        
        mockMvc.perform(put("/api/v1/book/{bookId}", bookId)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(expectedBook)));
        
        verify(bookService).update(bookId, requestDto);
    }

    @Test
    @SuppressWarnings("null")
    void shouldNotUpdateBookForAnon() throws Exception {        
        mockMvc.perform(put("/api/v1/book/{bookId}", 1L)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new UpdateBookRequestDto("Updated Book 1", 2L, Set.of(1L, 2L, 3L))
            )))
            .andExpect(status().isUnauthorized())
            .andExpect(content().json(objectMapper.writeValueAsString(getUnauthorizedResponseData())));        
    }

    @Test
    @WithMockUser
    void shouldReturn404WhenBookNotFound() throws Exception {
        Long bookId = 100L;
        when(bookService.findById(bookId)).thenThrow(new EntityNotFoundException("Book not found"));
        
        mockMvc.perform(get("/api/v1/book/{bookId}", bookId))
            .andExpect(status().isNotFound());
        
        verify(bookService).findById(bookId);
    }

    @Test
    @WithMockUser
    void shouldReturn500WhenRuntimeException() throws Exception {
        Long bookId = 1L;
        when(bookService.findById(bookId)).thenThrow(new RuntimeException("Internal error"));
        
        mockMvc.perform(get("/api/v1/book/{bookId}", bookId))
            .andExpect(status().isInternalServerError());
        
        verify(bookService).findById(bookId);
    }

    @Test
    @SuppressWarnings("null")
    @WithMockUser
    void shouldReturn400WhenInvalidInput() throws Exception {
        CreateBookRequestDto invalidRequest = new CreateBookRequestDto("", null, null);
        
        mockMvc.perform(post("/api/v1/book")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    private static List<BookDto> getDbBookDtos() {
        AuthorDto author1 = new AuthorDto(1L, "Author_1");
        AuthorDto author2 = new AuthorDto(2L, "Author_2");
        AuthorDto author3 = new AuthorDto(3L, "Author_3");
        
        GenreDto genre1 = new GenreDto(1L, "Genre_1");
        GenreDto genre2 = new GenreDto(2L, "Genre_2");
        GenreDto genre3 = new GenreDto(3L, "Genre_3");
        GenreDto genre4 = new GenreDto(4L, "Genre_4");
        GenreDto genre5 = new GenreDto(5L, "Genre_5");
        GenreDto genre6 = new GenreDto(6L, "Genre_6");
        
        return List.of(
            new BookDto(1L, "BookTitle_1", author1, Set.of(genre1, genre2)),
            new BookDto(2L, "BookTitle_2", author2, Set.of(genre3, genre4)),
            new BookDto(3L, "BookTitle_3", author3, Set.of(genre5, genre6))
        );
    }

    private static Stream<Arguments> getCreateBookTestData() {
        return Stream.of(
            Arguments.of(
                new CreateBookRequestDto("New Book 1", 1L, Set.of(1L, 2L)),
                new BookDto(10L, "New Book 1", new AuthorDto(1L, "Author_1"), Set.of(
                    new GenreDto(1L, "Genre_1"),
                    new GenreDto(2L, "Genre_2")
                ))
            ),
            Arguments.of(
                new CreateBookRequestDto("New Book 2", 2L, Set.of(3L)),
                new BookDto(11L, "New Book 2", new AuthorDto(2L, "Author_2"), Set.of(
                    new GenreDto(3L, "Genre_3")
                ))
            ),
            Arguments.of(
                new CreateBookRequestDto("New Book 3", 3L, Set.of(1L, 3L, 5L)),
                new BookDto(12L, "New Book 3", new AuthorDto(3L, "Author_3"), Set.of(
                    new GenreDto(1L, "Genre_1"),
                    new GenreDto(3L, "Genre_3"),
                    new GenreDto(5L, "Genre_5")
                ))
            )
        );
    }

    private static Stream<Arguments> getUpdateBookTestData() {
        return Stream.of(
            Arguments.of(
                new UpdateBookRequestDto("New Book 1", 2L, Set.of(1L, 2L, 3L)),
                1L,
                new BookDto(10L, "New Book 1", new AuthorDto(2L, "Author_1"), Set.of(
                    new GenreDto(1L, "Genre_1"),
                    new GenreDto(2L, "Genre_2"),
                    new GenreDto(3L, "Genre_3")
                ))
            )
        );
    }

    private static Map<String,String> getUnauthorizedResponseData() {
        return Map.of("error", "Unauthorized");
    }
}