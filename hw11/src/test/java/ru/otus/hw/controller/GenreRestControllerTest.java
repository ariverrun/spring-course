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
import ru.otus.hw.dto.CreateGenreRequestDto;
import ru.otus.hw.dto.CreatedEntityDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.UpdateGenreRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.GenreService;

@WebFluxTest(GenreRestController.class)
class GenreRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private GenreService genreService;

    @Test
    void shouldListAllGenres() throws Exception {
        var expectedResult = getDbGenreDtos();
        
        when(genreService.findAll()).thenReturn(Flux.fromIterable(expectedResult));
        
        webTestClient.get()
            .uri("/api/v1/genre")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(GenreDto.class)
            .isEqualTo(expectedResult);
        
        verify(genreService).findAll();
    }

    @ParameterizedTest
    @MethodSource("getDbGenreDtos")
    void shouldGetGenreById(GenreDto expectedResult) throws Exception {
        when(genreService.findById(expectedResult.id())).thenReturn(Mono.just(expectedResult));
        
        webTestClient.get()
            .uri("/api/v1/genre/{genreId}", expectedResult.id())
            .exchange()
            .expectStatus().isOk()
            .expectBody(GenreDto.class)
            .isEqualTo(expectedResult);
        
        verify(genreService).findById(expectedResult.id());
    }

    @ParameterizedTest
    @MethodSource("getDbGenreDtos")
    void shouldDeleteGenre(GenreDto expectedResult) throws Exception {
        when(genreService.deleteById(expectedResult.id())).thenReturn(Mono.empty());
        
        webTestClient.delete()
            .uri("/api/v1/genre/{genreId}", expectedResult.id())
            .exchange()
            .expectStatus().isNoContent();
        
        verify(genreService).deleteById(expectedResult.id());
    }

    @ParameterizedTest
    @MethodSource("getCreateGenreTestData")
    void shouldCreateGenre(CreateGenreRequestDto requestDto, GenreDto expectedGenre) throws Exception {
        when(genreService.insert(requestDto)).thenReturn(Mono.just(expectedGenre));

        webTestClient.post()
            .uri("/api/v1/genre")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(CreatedEntityDto.class)
            .isEqualTo(new CreatedEntityDto(expectedGenre.id()));
    
        verify(genreService).insert(requestDto);
    }

    @ParameterizedTest
    @MethodSource("getUpdateGenreTestData")
    void shouldUpdateGenre(UpdateGenreRequestDto requestDto, String genreId, GenreDto expectedGenre) throws Exception {
        when(genreService.update(genreId, requestDto)).thenReturn(Mono.just(expectedGenre));
        
        webTestClient.put()
            .uri("/api/v1/genre/{genreId}", genreId)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isOk()
            .expectBody(GenreDto.class)
            .isEqualTo(expectedGenre);
        
        verify(genreService).update(genreId, requestDto);
    }

    @Test
    void shouldReturn404WhenGenreNotFound() throws Exception {
        String genreId = "100";
        when(genreService.findById(genreId)).thenReturn(Mono.error(new EntityNotFoundException("Genre not found")));
        
        webTestClient.get()
            .uri("/api/v1/genre/{genreId}", genreId)
            .exchange()
            .expectStatus().isNotFound();
        
        verify(genreService).findById(genreId);
    }

    @Test
    void shouldReturn500WhenRuntimeException() throws Exception {
        String genreId = "1";
        when(genreService.findById(genreId)).thenReturn(Mono.error(new RuntimeException("Internal error")));
        
        webTestClient.get()
            .uri("/api/v1/genre/{genreId}", genreId)
            .exchange()
            .expectStatus().is5xxServerError();
        
        verify(genreService).findById(genreId);
    }

    @Test
    void shouldReturn400WhenInvalidInput() throws Exception {
        CreateGenreRequestDto invalidRequest = new CreateGenreRequestDto("");
        
        webTestClient.post()
            .uri("/api/v1/genre")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequest)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturn400WhenUpdateWithInvalidInput() throws Exception {
        UpdateGenreRequestDto invalidRequest = new UpdateGenreRequestDto("");
        String genreId = "1";
        
        webTestClient.put()
            .uri("/api/v1/genre/{genreId}", genreId)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequest)
            .exchange()
            .expectStatus().isBadRequest();
    }

    private static List<GenreDto> getDbGenreDtos() {
        return List.of(
            new GenreDto("1", "Genre_1"),
            new GenreDto("2", "Genre_2"),
            new GenreDto("3", "Genre_3"),
            new GenreDto("4", "Genre_4"),
            new GenreDto("5", "Genre_5"),
            new GenreDto("6", "Genre_6")
        );
    }

    private static Stream<Arguments> getCreateGenreTestData() {
        return Stream.of(
            Arguments.of(
                new CreateGenreRequestDto("New Genre 1"),
                new GenreDto("10", "New Genre 1")
            ),
            Arguments.of(
                new CreateGenreRequestDto("New Genre 2"),
                new GenreDto("11", "New Genre 2")
            ),
            Arguments.of(
                new CreateGenreRequestDto("New Genre 3"),
                new GenreDto("12", "New Genre 3")
            )
        );
    }

    private static Stream<Arguments> getUpdateGenreTestData() {
        return Stream.of(
            Arguments.of(
                new UpdateGenreRequestDto("Updated Genre 1"),
                "1",
                new GenreDto("1", "Updated Genre 1")
            ),
            Arguments.of(
                new UpdateGenreRequestDto("Updated Genre 2"),
                "2",
                new GenreDto("2", "Updated Genre 2")
            ),
            Arguments.of(
                new UpdateGenreRequestDto("Updated Genre 3"),
                "3",
                new GenreDto("3", "Updated Genre 3")
            ),
            Arguments.of(
                new UpdateGenreRequestDto("Updated Genre 4"),
                "4",
                new GenreDto("4", "Updated Genre 4")
            ),
            Arguments.of(
                new UpdateGenreRequestDto("Updated Genre 5"),
                "5",
                new GenreDto("5", "Updated Genre 5")
            ),
            Arguments.of(
                new UpdateGenreRequestDto("Updated Genre 6"),
                "6",
                new GenreDto("6", "Updated Genre 6")
            )
        );
    }
}