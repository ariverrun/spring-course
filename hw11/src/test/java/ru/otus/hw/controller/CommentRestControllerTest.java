package ru.otus.hw.controller;

import static org.mockito.ArgumentMatchers.any;
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
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CreateCommentDto;
import ru.otus.hw.dto.CreatedEntityDto;
import ru.otus.hw.dto.UpdateCommentDto;
import ru.otus.hw.dto.UpdateCommentRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.CommentService;

@WebFluxTest(CommentRestController.class)
class CommentRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CommentService commentService;

    @Test
    void shouldGetCommentsByBookId() throws Exception {
        String bookId = "1";
        var expectedResult = getDbCommentDtosByBookId(bookId);
        
        when(commentService.findByBookId(bookId)).thenReturn(Flux.fromIterable(expectedResult));
        
        webTestClient.get()
            .uri("/api/v1/comment?bookId={bookId}", bookId)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(CommentDto.class)
            .isEqualTo(expectedResult);
        
        verify(commentService).findByBookId(bookId);
    }

    @ParameterizedTest
    @MethodSource("getDbCommentDtos")
    void shouldGetCommentById(CommentDto expectedResult) throws Exception {
        when(commentService.findById(expectedResult.id())).thenReturn(Mono.just(expectedResult));
        
        webTestClient.get()
            .uri("/api/v1/comment/{commentId}", expectedResult.id())
            .exchange()
            .expectStatus().isOk()
            .expectBody(CommentDto.class)
            .isEqualTo(expectedResult);
        
        verify(commentService).findById(expectedResult.id());
    }

    @ParameterizedTest
    @MethodSource("getDbCommentDtos")
    void shouldDeleteComment(CommentDto expectedResult) throws Exception {
        when(commentService.deleteById(expectedResult.id())).thenReturn(Mono.empty());
        
        webTestClient.delete()
            .uri("/api/v1/comment/{commentId}", expectedResult.id())
            .exchange()
            .expectStatus().isNoContent();
        
        verify(commentService).deleteById(expectedResult.id());
    }

    @ParameterizedTest
    @MethodSource("getCreateCommentTestData")
    void shouldCreateComment(CreateCommentDto requestDto, CommentDto expectedComment) throws Exception {
        when(commentService.insert(requestDto)).thenReturn(Mono.just(expectedComment));

        webTestClient.post()
            .uri("/api/v1/comment")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(CreatedEntityDto.class)
            .isEqualTo(new CreatedEntityDto(expectedComment.id()));
    
        verify(commentService).insert(requestDto);
    }

    @ParameterizedTest
    @MethodSource("getUpdateCommentTestData")
    void shouldUpdateComment(UpdateCommentRequestDto requestDto, String commentId, CommentDto expectedComment) throws Exception {
        when(commentService.update(any(UpdateCommentDto.class))).thenReturn(Mono.just(expectedComment));
        
        webTestClient.put()
            .uri("/api/v1/comment/{commentId}", commentId)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isOk()
            .expectBody(CommentDto.class)
            .isEqualTo(expectedComment);
        
        verify(commentService).update(any(UpdateCommentDto.class));
    }

    @Test
    void shouldReturn404WhenCommentNotFound() throws Exception {
        String commentId = "100";
        when(commentService.findById(commentId)).thenReturn(Mono.error(new EntityNotFoundException("Comment not found")));
        
        webTestClient.get()
            .uri("/api/v1/comment/{commentId}", commentId)
            .exchange()
            .expectStatus().isNotFound();
        
        verify(commentService).findById(commentId);
    }

    @Test
    void shouldReturn500WhenRuntimeException() throws Exception {
        String commentId = "1";
        when(commentService.findById(commentId)).thenReturn(Mono.error(new RuntimeException("Internal error")));
        
        webTestClient.get()
            .uri("/api/v1/comment/{commentId}", commentId)
            .exchange()
            .expectStatus().is5xxServerError();
        
        verify(commentService).findById(commentId);
    }

    @Test
    void shouldReturn400WhenCreateWithInvalidInput() throws Exception {
        CreateCommentDto invalidRequest = new CreateCommentDto("", "1");
        
        webTestClient.post()
            .uri("/api/v1/comment")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequest)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturn400WhenCreateWithNullBookId() throws Exception {
        CreateCommentDto invalidRequest = new CreateCommentDto("Valid text", null);
        
        webTestClient.post()
            .uri("/api/v1/comment")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequest)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturn400WhenUpdateWithInvalidInput() throws Exception {
        UpdateCommentRequestDto invalidRequest = new UpdateCommentRequestDto("");
        String commentId = "1";
        
        webTestClient.put()
            .uri("/api/v1/comment/{commentId}", commentId)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequest)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturn400WhenBookIdMissingInListRequest() throws Exception {
        webTestClient.get()
            .uri("/api/v1/comment")
            .exchange()
            .expectStatus().isBadRequest();
    }

    private static List<CommentDto> getDbCommentDtosByBookId(String bookId) {
        return switch (bookId) {
            case "1" -> List.of(
                new CommentDto("1", "Comment_1", "1"),
                new CommentDto("2", "Comment_2", "1")
            );
            case "2" -> List.of(
                new CommentDto("3", "Comment_3", "2")
            );
            case "3" -> List.of(
                new CommentDto("4", "Comment_4", "3")
            );
            default -> List.of();
        };
    }

    private static List<CommentDto> getDbCommentDtos() {
        return List.of(
            new CommentDto("1", "Comment_1", "1"),
            new CommentDto("2", "Comment_2", "1"),
            new CommentDto("3", "Comment_3", "2"),
            new CommentDto("4", "Comment_4", "3")
        );
    }

    private static Stream<Arguments> getCreateCommentTestData() {
        return Stream.of(
            Arguments.of(
                new CreateCommentDto("Comment_1.1", "1"),
                new CommentDto("10", "Comment_1.1", "1")
            ),
            Arguments.of(
                new CreateCommentDto("Comment_2.1", "2"),
                new CommentDto("11", "Comment_2.1", "2")
            ),
            Arguments.of(
                new CreateCommentDto("Comment_3.1", "3"),
                new CommentDto("12", "Comment_3.1", "3")
            )
        );
    }

    private static Stream<Arguments> getUpdateCommentTestData() {
        return Stream.of(
            Arguments.of(
                new UpdateCommentRequestDto("Comment_1.2"),
                "1",
                new CommentDto("1", "Comment_1.2", "1")
            ),
            Arguments.of(
                new UpdateCommentRequestDto("Comment_2.2"),
                "2",
                new CommentDto("2", "Comment_2.2", "1")
            ),
            Arguments.of(
                new UpdateCommentRequestDto("Comment_2.2"),
                "3",
                new CommentDto("3", "Comment_2.2", "2")
            )
        );
    }
}