package ru.otus.hw.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
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
import ru.otus.hw.dto.CreateGenreRequestDto;
import ru.otus.hw.dto.CreatedEntityDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.UpdateGenreRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.GenreService;

@WebMvcTest(GenreRestController.class)
@Import(SecurityConfig.class)
class GenreRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GenreService genreService;

    @Test
    @SuppressWarnings("null")
    @WithMockUser
    void shouldListAllGenres() throws Exception {
        var expectedResult = getDbGenreDtos();
        
        when(genreService.findAll()).thenReturn(expectedResult);
        
        mockMvc.perform(get("/api/v1/genre"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));
        
        verify(genreService).findAll();
    }

    @Test
    @SuppressWarnings("null")
    void shouldNotListAllGenresForAnon() throws Exception {                
        mockMvc.perform(get("/api/v1/genre"))
            .andExpect(status().isUnauthorized())
            .andExpect(content().json(objectMapper.writeValueAsString(getUnauthorizedResponseData())));        
    }

    @ParameterizedTest
    @MethodSource("getDbGenreDtos")
    @SuppressWarnings("null")
    @WithMockUser
    void shouldGetGenreById(GenreDto expectedResult) throws Exception {
        when(genreService.findById(expectedResult.id())).thenReturn(expectedResult);
        
        mockMvc.perform(get("/api/v1/genre/{genreId}", expectedResult.id()))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));
        
        verify(genreService).findById(expectedResult.id());
    }

    @Test
    @SuppressWarnings("null")
    void shouldNotGetGenreByIdForAnon() throws Exception {        
        mockMvc.perform(get("/api/v1/genre/{genreId}", 1L))
            .andExpect(status().isUnauthorized())
            .andExpect(content().json(objectMapper.writeValueAsString(getUnauthorizedResponseData())));        
    }

    @ParameterizedTest
    @MethodSource("getDbGenreDtos")
    @WithMockUser
    void shouldDeleteGenre(GenreDto expectedResult) throws Exception {
        mockMvc.perform(delete("/api/v1/genre/{genreId}", expectedResult.id()))
            .andExpect(status().isNoContent());
        
        verify(genreService).deleteById(expectedResult.id());
    }

    @Test
    @SuppressWarnings("null")
    void shouldNotDeleteGenreForAnon() throws Exception {
        mockMvc.perform(delete("/api/v1/genre/{genreId}", 1L))
            .andExpect(status().isUnauthorized())
            .andExpect(content().json(objectMapper.writeValueAsString(getUnauthorizedResponseData())));        
    }

    @ParameterizedTest
    @MethodSource("getCreateGenreTestData")
    @SuppressWarnings("null")
    @WithMockUser
    void shouldCreateGenre(CreateGenreRequestDto requestDto, GenreDto expectedGenre) throws Exception {
        when(genreService.insert(requestDto)).thenReturn(expectedGenre);

        mockMvc.perform(post("/api/v1/genre")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(
                new CreatedEntityDto(expectedGenre.id())
            )));
    
        verify(genreService).insert(requestDto);
    }

    @Test
    @SuppressWarnings("null")
    void shouldNotCreateGenreForAnon() throws Exception {
        mockMvc.perform(post("/api/v1/genre")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new CreateGenreRequestDto("New Genre 1")
            )))
            .andExpect(status().isUnauthorized())
            .andExpect(content().json(objectMapper.writeValueAsString(getUnauthorizedResponseData())));    
    }

    @ParameterizedTest
    @MethodSource("getUpdateGenreTestData")
    @SuppressWarnings("null")
    @WithMockUser
    void shouldUpdateGenre(UpdateGenreRequestDto requestDto, Long genreId, GenreDto expectedGenre) throws Exception {
        when(genreService.update(genreId, requestDto)).thenReturn(expectedGenre);
        
        mockMvc.perform(put("/api/v1/genre/{genreId}", genreId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(expectedGenre)));
        
        verify(genreService).update(genreId, requestDto);
    }

    @Test
    @SuppressWarnings("null")
    void shouldNotUpdateGenreForAnon() throws Exception {        
        mockMvc.perform(put("/api/v1/genre/{genreId}", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new UpdateGenreRequestDto("Updated Genre 1")
            )))
            .andExpect(status().isUnauthorized())
            .andExpect(content().json(objectMapper.writeValueAsString(getUnauthorizedResponseData())));        
    }

    @Test
    @WithMockUser
    void shouldReturn404WhenGenreNotFound() throws Exception {
        Long genreId = 100L;
        when(genreService.findById(genreId)).thenThrow(new EntityNotFoundException("Genre not found"));
        
        mockMvc.perform(get("/api/v1/genre/{genreId}", genreId))
            .andExpect(status().isNotFound());
        
        verify(genreService).findById(genreId);
    }

    @Test
    @WithMockUser
    void shouldReturn500WhenRuntimeException() throws Exception {
        Long genreId = 1L;
        when(genreService.findById(genreId)).thenThrow(new RuntimeException("Internal error"));
        
        mockMvc.perform(get("/api/v1/genre/{genreId}", genreId))
            .andExpect(status().isInternalServerError());
        
        verify(genreService).findById(genreId);
    }

    @Test
    @SuppressWarnings("null")
    @WithMockUser
    void shouldReturn400WhenInvalidInput() throws Exception {
        CreateGenreRequestDto invalidRequest = new CreateGenreRequestDto("");
        
        mockMvc.perform(post("/api/v1/genre")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SuppressWarnings("null")
    @WithMockUser
    void shouldReturn400WhenUpdateWithInvalidInput() throws Exception {
        UpdateGenreRequestDto invalidRequest = new UpdateGenreRequestDto("");
        Long genreId = 1L;
        
        mockMvc.perform(put("/api/v1/genre/{genreId}", genreId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    private static List<GenreDto> getDbGenreDtos() {
        return List.of(
            new GenreDto(1L, "Genre_1"),
            new GenreDto(2L, "Genre_2"),
            new GenreDto(3L, "Genre_3"),
            new GenreDto(4L, "Genre_4"),
            new GenreDto(5L, "Genre_5"),
            new GenreDto(6L, "Genre_6")
        );
    }

    private static Stream<Arguments> getCreateGenreTestData() {
        return Stream.of(
            Arguments.of(
                new CreateGenreRequestDto("New Genre 1"),
                new GenreDto(10L, "New Genre 1")
            ),
            Arguments.of(
                new CreateGenreRequestDto("New Genre 2"),
                new GenreDto(11L, "New Genre 2")
            ),
            Arguments.of(
                new CreateGenreRequestDto("New Genre 3"),
                new GenreDto(12L, "New Genre 3")
            )
        );
    }

    private static Stream<Arguments> getUpdateGenreTestData() {
        return Stream.of(
            Arguments.of(
                new UpdateGenreRequestDto("Updated Genre 1"),
                1L,
                new GenreDto(1L, "Updated Genre 1")
            ),
            Arguments.of(
                new UpdateGenreRequestDto("Updated Genre 2"),
                2L,
                new GenreDto(2L, "Updated Genre 2")
            ),
            Arguments.of(
                new UpdateGenreRequestDto("Updated Genre 3"),
                3L,
                new GenreDto(3L, "Updated Genre 3")
            ),
            Arguments.of(
                new UpdateGenreRequestDto("Updated Genre 4"),
                4L,
                new GenreDto(4L, "Updated Genre 4")
            ),
            Arguments.of(
                new UpdateGenreRequestDto("Updated Genre 5"),
                5L,
                new GenreDto(5L, "Updated Genre 5")
            ),
            Arguments.of(
                new UpdateGenreRequestDto("Updated Genre 6"),
                6L,
                new GenreDto(6L, "Updated Genre 6")
            )
        );
    }

    private static Map<String, String> getUnauthorizedResponseData() {
        return Map.of("error", "Unauthorized");
    }
}