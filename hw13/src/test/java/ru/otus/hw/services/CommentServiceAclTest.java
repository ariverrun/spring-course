package ru.otus.hw.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import ru.otus.hw.dto.UpdateCommentDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class CommentServiceAclTest {

    @Autowired
    private CommentService commentService;

    @Test
    @WithMockUser(username = "user1")
    void findByBookIdReturnsCommentsForUserWithReadOnAll() {
        assertThat(commentService.findByBookId(1L)).hasSize(2);
    }

    @Test
    @WithMockUser(username = "user2")
    void findByBookIdReturnsCommentsForReadOnlyUser() {
        assertThat(commentService.findByBookId(1L)).hasSize(2);
    }

    @Test
    @WithMockUser(username = "user3")
    void findByBookIdReturnsAllCommentsForAnyUser() {
        assertThat(commentService.findByBookId(1L)).hasSize(2);
    }

    @Test
    @WithMockUser(username = "user1")
    void findByIdSucceedsForUserWithReadPermission() {
        var result = commentService.findById(1L);
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    @WithMockUser(username = "user2")
    void findByIdSucceedsForReadOnlyUser() {
        assertThat(commentService.findById(1L)).isNotNull();
    }

    @Test
    @WithMockUser(username = "user3")
    void findByIdSucceedsForAnyUser() {
        assertThat(commentService.findById(1L)).isNotNull();
    }

    @Test
    @WithMockUser(username = "user1")
    void updateSucceedsForUserWithWritePermission() {
        var dto = new UpdateCommentDto(1L, "Text_1.1");
        var result = commentService.update(dto);
        assertThat(result.text()).isEqualTo("Text_1.1");
    }

    @Test
    @WithMockUser(username = "user2")
    void updateThrowsForUserWithoutWritePermission() {
        var dto = new UpdateCommentDto(1L, "Text_1.2");
        assertThatThrownBy(() -> commentService.update(dto))
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateThrowsForAdminWithoutAclPermission() {
        var dto = new UpdateCommentDto(1L, "Text_1.3");
        assertThatThrownBy(() -> commentService.update(dto))
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(username = "user1")
    void deleteSucceedsForUserWithDeletePermission() {
        commentService.deleteById(4L);
    }

    @Test
    @WithMockUser(username = "user2")
    void deleteThrowsForUserWithoutDeletePermission() {
        assertThatThrownBy(() -> commentService.deleteById(4L))
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteThrowsForAdminWithoutAclPermission() {
        assertThatThrownBy(() -> commentService.deleteById(4L))
            .isInstanceOf(AccessDeniedException.class);
    }
}
