package ru.otus.hw.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
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
import ru.otus.hw.dto.CreateAuthorRequestDto;
import ru.otus.hw.dto.CreatedEntityDto;
import ru.otus.hw.dto.UpdateAuthorRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;

@WebFluxTest(AuthorRestController.class)
class AuthorRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AuthorService authorService;

    @Test
    void shouldListAllAuthors() throws Exception {
        var expectedResult = getDbAuthorDtos();
        
        when(authorService.findAll()).thenReturn(Flux.fromIterable(expectedResult));
        
        webTestClient.get()
            .uri("/api/v1/author")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(AuthorDto.class)
            .isEqualTo(expectedResult);
        
        verify(authorService).findAll();
    }

    @ParameterizedTest
    @MethodSource("getDbAuthorDtos")
    void shouldGetAuthorById(AuthorDto expectedResult) throws Exception {
        when(authorService.findById(expectedResult.id())).thenReturn(Mono.just(expectedResult));
        
        webTestClient.get()
            .uri("/api/v1/author/{authorId}", expectedResult.id())
            .exchange()
            .expectStatus().isOk()
            .expectBody(AuthorDto.class)
            .isEqualTo(expectedResult);
        
        verify(authorService).findById(expectedResult.id());
    }

    @ParameterizedTest
    @MethodSource("getDbAuthorDtos")
    void shouldDeleteAuthor(AuthorDto expectedResult) throws Exception {
        when(authorService.deleteById(expectedResult.id())).thenReturn(Mono.empty());
        
        webTestClient.delete()
            .uri("/api/v1/author/{authorId}", expectedResult.id())
            .exchange()
            .expectStatus().isNoContent();
        
        verify(authorService).deleteById(expectedResult.id());
    }

    @ParameterizedTest
    @MethodSource("getCreateAuthorTestData")
    void shouldCreateAuthor(CreateAuthorRequestDto requestDto, AuthorDto expectedAuthor) throws Exception {
        when(authorService.insert(requestDto)).thenReturn(Mono.just(expectedAuthor));

        webTestClient.post()
            .uri("/api/v1/author")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(CreatedEntityDto.class)
            .isEqualTo(new CreatedEntityDto(expectedAuthor.id()));
    
        verify(authorService).insert(requestDto);
    }

    @ParameterizedTest
    @MethodSource("getUpdateAuthorTestData")
    void shouldUpdateAuthor(UpdateAuthorRequestDto requestDto, Long authorId, AuthorDto expectedAuthor) throws Exception {
        when(authorService.update(String.valueOf(authorId), requestDto)).thenReturn(Mono.just(expectedAuthor));
        
        webTestClient.put()
            .uri("/api/v1/author/{authorId}", String.valueOf(authorId))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isOk()
            .expectBody(AuthorDto.class)
            .isEqualTo(expectedAuthor);
        
        verify(authorService).update(String.valueOf(authorId), requestDto);
    }

    @Test
    void shouldReturn404WhenAuthorNotFound() throws Exception {
        Long authorId = 100L;
        when(authorService.findById(String.valueOf(authorId))).thenReturn(Mono.error(new EntityNotFoundException("Author not found")));
        
        webTestClient.get()
            .uri("/api/v1/author/{authorId}", String.valueOf(authorId))
            .exchange()
            .expectStatus().isNotFound();
        
        verify(authorService).findById(String.valueOf(authorId));
    }

    @Test
    void shouldReturn500WhenRuntimeException() throws Exception {
        Long authorId = 1L;
        when(authorService.findById(String.valueOf(authorId))).thenReturn(Mono.error(new RuntimeException("Internal error")));
        
        webTestClient.get()
            .uri("/api/v1/author/{authorId}", String.valueOf(authorId))
            .exchange()
            .expectStatus().is5xxServerError();
        
        verify(authorService).findById(String.valueOf(authorId));
    }

    @Test
    void shouldReturn400WhenInvalidInput() throws Exception {
        CreateAuthorRequestDto invalidRequest = new CreateAuthorRequestDto("");
        
        webTestClient.post()
            .uri("/api/v1/author")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequest)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturn400WhenUpdateWithInvalidInput() throws Exception {
        UpdateAuthorRequestDto invalidRequest = new UpdateAuthorRequestDto("");
        Long authorId = 1L;
        
        webTestClient.put()
            .uri("/api/v1/author/{authorId}", String.valueOf(authorId))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequest)
            .exchange()
            .expectStatus().isBadRequest();
    }

    private static List<AuthorDto> getDbAuthorDtos() {
        return List.of(
            new AuthorDto("1", "Author_1"),
            new AuthorDto("2", "Author_2"),
            new AuthorDto("3", "Author_3")
        );
    }

    private static Stream<Arguments> getCreateAuthorTestData() {
        return Stream.of(
            Arguments.of(
                new CreateAuthorRequestDto("New Author 1"),
                new AuthorDto("10", "New Author 1")
            ),
            Arguments.of(
                new CreateAuthorRequestDto("New Author 2"),
                new AuthorDto("11", "New Author 2")
            ),
            Arguments.of(
                new CreateAuthorRequestDto("New Author 3"),
                new AuthorDto("12", "New Author 3")
            )
        );
    }

    private static Stream<Arguments> getUpdateAuthorTestData() {
        return Stream.of(
            Arguments.of(
                new UpdateAuthorRequestDto("Updated Author 1"),
                1L,
                new AuthorDto("1", "Updated Author 1")
            ),
            Arguments.of(
                new UpdateAuthorRequestDto("Updated Author 2"),
                2L,
                new AuthorDto("2", "Updated Author 2")
            ),
            Arguments.of(
                new UpdateAuthorRequestDto("Updated Author 3"),
                3L,
                new AuthorDto("3", "Updated Author 3")
            )
        );
    }
}