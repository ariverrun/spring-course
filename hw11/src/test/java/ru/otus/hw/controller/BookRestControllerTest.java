package ru.otus.hw.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CreateBookRequestDto;
import ru.otus.hw.dto.CreatedEntityDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.UpdateBookRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.BookService;

@WebFluxTest(BookRestController.class)
class BookRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BookService bookService;

    @Test
    void shouldListAllBooks() throws Exception {
        var expectedResult = getDbBookDtos();
        
        when(bookService.findAll()).thenReturn(Flux.fromIterable(expectedResult));
        
        webTestClient.get()
            .uri("/api/v1/book")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(BookDto.class)
            .isEqualTo(expectedResult);
        
        verify(bookService).findAll();
    }

    @ParameterizedTest
    @MethodSource("getDbBookDtos")
    void shouldGetBookById(BookDto expectedResult) throws Exception {
        when(bookService.findById(expectedResult.id())).thenReturn(Mono.just(expectedResult));
        
        webTestClient.get()
            .uri("/api/v1/book/{bookId}", expectedResult.id())
            .exchange()
            .expectStatus().isOk()
            .expectBody(BookDto.class)
            .isEqualTo(expectedResult);
        
        verify(bookService).findById(expectedResult.id());
    }
 
    @ParameterizedTest
    @MethodSource("getDbBookDtos")
    void shouldDeleteBook(BookDto expectedResult) throws Exception {
        when(bookService.deleteById(expectedResult.id())).thenReturn(Mono.empty());
        
        webTestClient.delete()
            .uri("/api/v1/book/{bookId}", expectedResult.id())
            .exchange()
            .expectStatus().isNoContent();
        
        verify(bookService).deleteById(expectedResult.id());
    }

    @ParameterizedTest
    @MethodSource("getCreateBookTestData")
    void shouldCreateBook(CreateBookRequestDto requestDto, BookDto expectedBook) throws Exception {
        when(bookService.insert(requestDto)).thenReturn(Mono.just(expectedBook));

        webTestClient.post()
            .uri("/api/v1/book")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(CreatedEntityDto.class)
            .isEqualTo(new CreatedEntityDto(expectedBook.id()));
    
        verify(bookService).insert(requestDto);
    }

    @ParameterizedTest
    @MethodSource("getUpdateBookTestData")
    void shouldUpdateBook(UpdateBookRequestDto requestDto, Long bookId, BookDto expectedBook) throws Exception {
        when(bookService.update(String.valueOf(bookId), requestDto)).thenReturn(Mono.just(expectedBook));
        
        webTestClient.put()
            .uri("/api/v1/book/{bookId}", String.valueOf(bookId))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isOk()
            .expectBody(BookDto.class)
            .isEqualTo(expectedBook);
        
        verify(bookService).update(String.valueOf(bookId), requestDto);
    }

    @Test
    void shouldReturn404WhenBookNotFound() throws Exception {
        Long bookId = 100L;
        when(bookService.findById(String.valueOf(bookId))).thenReturn(Mono.error(new EntityNotFoundException("Book not found")));
        
        webTestClient.get()
            .uri("/api/v1/book/{bookId}", String.valueOf(bookId))
            .exchange()
            .expectStatus().isNotFound();
        
        verify(bookService).findById(String.valueOf(bookId));
    }

    @Test
    void shouldReturn500WhenRuntimeException() throws Exception {
        Long bookId = 1L;
        when(bookService.findById(String.valueOf(bookId))).thenReturn(Mono.error(new RuntimeException("Internal error")));
        
        webTestClient.get()
            .uri("/api/v1/book/{bookId}", String.valueOf(bookId))
            .exchange()
            .expectStatus().is5xxServerError();
        
        verify(bookService).findById(String.valueOf(bookId));
    }

    @Test
    void shouldReturn400WhenInvalidInput() throws Exception {
        CreateBookRequestDto invalidRequest = new CreateBookRequestDto("", null, null);
        
        webTestClient.post()
            .uri("/api/v1/book")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequest)
            .exchange()
            .expectStatus().isBadRequest();
    }

    private static List<BookDto> getDbBookDtos() {
        AuthorDto author1 = new AuthorDto("1", "Author_1");
        AuthorDto author2 = new AuthorDto("2", "Author_2");
        AuthorDto author3 = new AuthorDto("3", "Author_3");
        
        GenreDto genre1 = new GenreDto("1", "Genre_1");
        GenreDto genre2 = new GenreDto("2", "Genre_2");
        GenreDto genre3 = new GenreDto("3", "Genre_3");
        GenreDto genre4 = new GenreDto("4", "Genre_4");
        GenreDto genre5 = new GenreDto("5", "Genre_5");
        GenreDto genre6 = new GenreDto("6", "Genre_6");
        
        return List.of(
            new BookDto("1", "BookTitle_1", author1, Set.of(genre1, genre2)),
            new BookDto("2", "BookTitle_2", author2, Set.of(genre3, genre4)),
            new BookDto("3", "BookTitle_3", author3, Set.of(genre5, genre6))
        );
    }

    private static Stream<Arguments> getCreateBookTestData() {
        return Stream.of(
            Arguments.of(
                new CreateBookRequestDto("New Book 1", "1", Set.of("1", "2")),
                new BookDto("10", "New Book 1", new AuthorDto("1", "Author_1"), Set.of(
                    new GenreDto("1", "Genre_1"),
                    new GenreDto("2", "Genre_2")
                ))
            ),
            Arguments.of(
                new CreateBookRequestDto("New Book 2", "2", Set.of("3")),
                new BookDto("11", "New Book 2", new AuthorDto("2", "Author_2"), Set.of(
                    new GenreDto("3", "Genre_3")
                ))
            ),
            Arguments.of(
                new CreateBookRequestDto("New Book 3", "3", Set.of("1", "3", "5")),
                new BookDto("12", "New Book 3", new AuthorDto("3", "Author_3"), Set.of(
                    new GenreDto("1", "Genre_1"),
                    new GenreDto("3", "Genre_3"),
                    new GenreDto("5", "Genre_5")
                ))
            )
        );
    }

    private static Stream<Arguments> getUpdateBookTestData() {
        return Stream.of(
            Arguments.of(
                new UpdateBookRequestDto("New Book 1", "2", Set.of("1", "2", "3")),
                1L,
                new BookDto("10", "New Book 1", new AuthorDto("2", "Author_1"), Set.of(
                    new GenreDto("1", "Genre_1"),
                    new GenreDto("2", "Genre_2"),
                    new GenreDto("3", "Genre_3")
                ))
            )
        );
    }
}