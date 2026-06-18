package ru.otus.hw.controller;

import static org.mockito.ArgumentMatchers.any;
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

import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CreateCommentDto;
import ru.otus.hw.dto.UpdateCommentDto;
import ru.otus.hw.dto.UpdateCommentRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.CommentService;

@WebMvcTest(CommentRestController.class)
class CommentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @Test
    @SuppressWarnings("null")
    void shouldGetCommentsByBookId() throws Exception {
        Long bookId = 1L;
        var expectedResult = getDbCommentDtosByBookId(bookId);
        
        when(commentService.findByBookId(bookId)).thenReturn(expectedResult);
        
        mockMvc.perform(get("/api/v1/comment")
            .param("bookId", String.valueOf(bookId)))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));
        
        verify(commentService).findByBookId(bookId);
    }

    @ParameterizedTest
    @MethodSource("getDbCommentDtos")
    @SuppressWarnings("null")
    void shouldGetCommentById(CommentDto expectedResult) throws Exception {
        when(commentService.findById(expectedResult.id())).thenReturn(expectedResult);
        
        mockMvc.perform(get("/api/v1/comment/{commentId}", expectedResult.id()))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));
        
        verify(commentService).findById(expectedResult.id());
    }

    @ParameterizedTest
    @MethodSource("getDbCommentDtos")
    void shouldDeleteComment(CommentDto expectedResult) throws Exception {
        mockMvc.perform(delete("/api/v1/comment/{commentId}", expectedResult.id()))
            .andExpect(status().isNoContent());
        
        verify(commentService).deleteById(expectedResult.id());
    }

    @ParameterizedTest
    @MethodSource("getCreateCommentTestData")
    @SuppressWarnings("null")
    void shouldCreateComment(CreateCommentDto requestDto, CommentDto expectedComment) throws Exception {
        when(commentService.insert(requestDto)).thenReturn(expectedComment);

        mockMvc.perform(post("/api/v1/comment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(expectedComment)));
    
        verify(commentService).insert(requestDto);
    }

    @ParameterizedTest
    @MethodSource("getUpdateCommentTestData")
    @SuppressWarnings("null")
    void shouldUpdateComment(UpdateCommentRequestDto requestDto, Long commentId, CommentDto expectedComment) throws Exception {
        when(commentService.update(any(UpdateCommentDto.class))).thenReturn(expectedComment);
        
        mockMvc.perform(put("/api/v1/comment/{commentId}", commentId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(expectedComment)));
        
        verify(commentService).update(any(UpdateCommentDto.class));
    }

    @Test
    void shouldReturn404WhenCommentNotFound() throws Exception {
        Long commentId = 100L;
        when(commentService.findById(commentId)).thenThrow(new EntityNotFoundException("Comment not found"));
        
        mockMvc.perform(get("/api/v1/comment/{commentId}", commentId))
            .andExpect(status().isNotFound());
        
        verify(commentService).findById(commentId);
    }

    @Test
    void shouldReturn500WhenRuntimeException() throws Exception {
        Long commentId = 1L;
        when(commentService.findById(commentId)).thenThrow(new RuntimeException("Internal error"));
        
        mockMvc.perform(get("/api/v1/comment/{commentId}", commentId))
            .andExpect(status().isInternalServerError());
        
        verify(commentService).findById(commentId);
    }

    @Test
    @SuppressWarnings("null")
    void shouldReturn400WhenCreateWithInvalidInput() throws Exception {
        CreateCommentDto invalidRequest = new CreateCommentDto("", 1L);
        
        mockMvc.perform(post("/api/v1/comment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SuppressWarnings("null")
    void shouldReturn400WhenCreateWithNullBookId() throws Exception {
        CreateCommentDto invalidRequest = new CreateCommentDto("Valid text", null);
        
        mockMvc.perform(post("/api/v1/comment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SuppressWarnings("null")
    void shouldReturn400WhenUpdateWithInvalidInput() throws Exception {
        UpdateCommentRequestDto invalidRequest = new UpdateCommentRequestDto("");
        Long commentId = 1L;
        
        mockMvc.perform(put("/api/v1/comment/{commentId}", commentId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenBookIdMissingInListRequest() throws Exception {
        mockMvc.perform(get("/api/v1/comment"))
            .andExpect(status().isBadRequest());
    }

    private static List<CommentDto> getDbCommentDtosByBookId(Long bookId) {
        return switch (bookId.intValue()) {
            case 1 -> List.of(
                new CommentDto(1L, "Comment_1", 1L, "BookTitle_1"),
                new CommentDto(2L, "Comment_2", 1L, "BookTitle_1")
            );
            case 2 -> List.of(
                new CommentDto(3L, "Comment_3", 2L, "BookTitle_2")
            );
            case 3 -> List.of(
                new CommentDto(4L, "Comment_4", 3L, "BookTitle_3")
            );
            default -> List.of();
        };
    }

    private static List<CommentDto> getDbCommentDtos() {
        return List.of(
            new CommentDto(1L, "Comment_1", 1L, "BookTitle_1"),
            new CommentDto(2L, "Comment_2", 1L, "BookTitle_1"),
            new CommentDto(3L, "Comment_3", 2L, "BookTitle_2"),
            new CommentDto(4L, "Comment_4", 3L, "BookTitle_3")
        );
    }

    private static Stream<Arguments> getCreateCommentTestData() {
        return Stream.of(
            Arguments.of(
                new CreateCommentDto("Comment_1.1", 1L),
                new CommentDto(10L, "Comment_1.1", 1L, "BookTitle_1")
            ),
            Arguments.of(
                new CreateCommentDto("Comment_2.1", 2L),
                new CommentDto(11L, "Comment_2.1", 2L, "BookTitle_2")
            ),
            Arguments.of(
                new CreateCommentDto("Comment_3.1", 3L),
                new CommentDto(12L, "Comment_3.1", 3L, "BookTitle_3")
            )
        );
    }

    private static Stream<Arguments> getUpdateCommentTestData() {
        return Stream.of(
            Arguments.of(
                new UpdateCommentRequestDto("Comment_1.2"),
                1L,
                new CommentDto(1L, "Comment_1.2", 1L, "BookTitle_1")
            ),
            Arguments.of(
                new UpdateCommentRequestDto("Comment_2.2"),
                2L,
                new CommentDto(2L, "Comment_2.2", 1L, "BookTitle_1")
            ),
            Arguments.of(
                new UpdateCommentRequestDto("Comment_2.2"),
                3L,
                new CommentDto(3L, "Comment_2.2", 2L, "BookTitle_2")
            )
        );
    }
}