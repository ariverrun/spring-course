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
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.CreateAuthorRequestDto;
import ru.otus.hw.dto.CreatedEntityDto;
import ru.otus.hw.dto.UpdateAuthorRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;

@WebMvcTest(AuthorRestController.class)
class AuthorRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthorService authorService;

    @Test
    @SuppressWarnings("null")
    void shouldListAllAuthors() throws Exception {
        var expectedResult = getDbAuthorDtos();
        
        when(authorService.findAll()).thenReturn(expectedResult);
        
        mockMvc.perform(get("/api/v1/author"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));
        
        verify(authorService).findAll();
    }

    @ParameterizedTest
    @MethodSource("getDbAuthorDtos")
    @SuppressWarnings("null")
    void shouldGetAuthorById(AuthorDto expectedResult) throws Exception {
        when(authorService.findById(expectedResult.id())).thenReturn(expectedResult);
        
        mockMvc.perform(get("/api/v1/author/{authorId}", expectedResult.id()))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));
        
        verify(authorService).findById(expectedResult.id());
    }

    @ParameterizedTest
    @MethodSource("getDbAuthorDtos")
    void shouldDeleteAuthor(AuthorDto expectedResult) throws Exception {
        mockMvc.perform(delete("/api/v1/author/{authorId}", expectedResult.id()))
            .andExpect(status().isNoContent());
        
        verify(authorService).deleteById(expectedResult.id());
    }

    @ParameterizedTest
    @MethodSource("getCreateAuthorTestData")
    @SuppressWarnings("null")
    void shouldCreateAuthor(CreateAuthorRequestDto requestDto, AuthorDto expectedAuthor) throws Exception {
        when(authorService.insert(requestDto)).thenReturn(expectedAuthor);

        mockMvc.perform(post("/api/v1/author")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(
                new CreatedEntityDto(expectedAuthor.id())
            )));
    
        verify(authorService).insert(requestDto);
    }

    @ParameterizedTest
    @MethodSource("getUpdateAuthorTestData")
    @SuppressWarnings("null")
    void shouldUpdateAuthor(UpdateAuthorRequestDto requestDto, Long authorId, AuthorDto expectedAuthor) throws Exception {
        when(authorService.update(authorId, requestDto)).thenReturn(expectedAuthor);
        
        mockMvc.perform(put("/api/v1/author/{authorId}", authorId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(expectedAuthor)));
        
        verify(authorService).update(authorId, requestDto);
    }

    @Test
    void shouldReturn404WhenAuthorNotFound() throws Exception {
        Long authorId = 100L;
        when(authorService.findById(authorId)).thenThrow(new EntityNotFoundException("Author not found"));
        
        mockMvc.perform(get("/api/v1/author/{authorId}", authorId))
            .andExpect(status().isNotFound());
        
        verify(authorService).findById(authorId);
    }

    @Test
    void shouldReturn500WhenRuntimeException() throws Exception {
        Long authorId = 1L;
        when(authorService.findById(authorId)).thenThrow(new RuntimeException("Internal error"));
        
        mockMvc.perform(get("/api/v1/author/{authorId}", authorId))
            .andExpect(status().isInternalServerError());
        
        verify(authorService).findById(authorId);
    }

    @Test
    @SuppressWarnings("null")
    void shouldReturn400WhenInvalidInput() throws Exception {
        CreateAuthorRequestDto invalidRequest = new CreateAuthorRequestDto("");
        
        mockMvc.perform(post("/api/v1/author")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SuppressWarnings("null")
    void shouldReturn400WhenUpdateWithInvalidInput() throws Exception {
        UpdateAuthorRequestDto invalidRequest = new UpdateAuthorRequestDto("");
        Long authorId = 1L;
        
        mockMvc.perform(put("/api/v1/author/{authorId}", authorId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    private static List<AuthorDto> getDbAuthorDtos() {
        return List.of(
            new AuthorDto(1L, "Author_1"),
            new AuthorDto(2L, "Author_2"),
            new AuthorDto(3L, "Author_3")
        );
    }

    private static Stream<Arguments> getCreateAuthorTestData() {
        return Stream.of(
            Arguments.of(
                new CreateAuthorRequestDto("New Author 1"),
                new AuthorDto(10L, "New Author 1")
            ),
            Arguments.of(
                new CreateAuthorRequestDto("New Author 2"),
                new AuthorDto(11L, "New Author 2")
            ),
            Arguments.of(
                new CreateAuthorRequestDto("New Author 3"),
                new AuthorDto(12L, "New Author 3")
            )
        );
    }

    private static Stream<Arguments> getUpdateAuthorTestData() {
        return Stream.of(
            Arguments.of(
                new UpdateAuthorRequestDto("Updated Author 1"),
                1L,
                new AuthorDto(1L, "Updated Author 1")
            ),
            Arguments.of(
                new UpdateAuthorRequestDto("Updated Author 2"),
                2L,
                new AuthorDto(2L, "Updated Author 2")
            ),
            Arguments.of(
                new UpdateAuthorRequestDto("Updated Author 3"),
                3L,
                new AuthorDto(3L, "Updated Author 3")
            )
        );
    }
}