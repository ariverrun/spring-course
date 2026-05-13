package ru.otus.hw.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import ru.otus.hw.dto.UpdateAuthorRequestDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class AuthorServiceAclTest {

    @Autowired
    private AuthorService authorService;

    @Test
    @WithMockUser(username = "user1")
    void findAllReturnsAllAuthorsForUserWithReadOnAll() {
        assertThat(authorService.findAll()).hasSize(3);
    }

    @Test
    @WithMockUser(username = "user2")
    void findAllReturnsAllAuthorsForReadOnlyUser() {
        assertThat(authorService.findAll()).hasSize(3);
    }

    @Test
    @WithMockUser(username = "user3")
    void findAllReturnsAllAuthorsForAnyUser() {
        assertThat(authorService.findAll()).hasSize(3);
    }

    @Test
    @WithMockUser(username = "user1")
    void findByIdSucceedsForUserWithReadPermission() {
        var result = authorService.findById(1L);
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    @WithMockUser(username = "user2")
    void findByIdSucceedsForReadOnlyUser() {
        assertThat(authorService.findById(1L)).isNotNull();
    }

    @Test
    @WithMockUser(username = "user3")
    void findByIdSucceedsForAnyUser() {
        assertThat(authorService.findById(1L)).isNotNull();
    }

    @Test
    @WithMockUser(username = "user1")
    void updateSucceedsForUserWithWritePermission() {
        var dto = new UpdateAuthorRequestDto("Author_1.1");
        var result = authorService.update(1L, dto);
        assertThat(result.fullName()).isEqualTo("Author_1.1");
    }

    @Test
    @WithMockUser(username = "user2")
    void updateThrowsForUserWithoutWritePermission() {
        var dto = new UpdateAuthorRequestDto("Author_1.2");
        assertThatThrownBy(() -> authorService.update(1L, dto))
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateThrowsForAdminWithoutAclPermission() {
        var dto = new UpdateAuthorRequestDto("Author_1.3");
        assertThatThrownBy(() -> authorService.update(1L, dto))
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(username = "user1")
    void deleteSucceedsForUserWithDeletePermission() {
        authorService.deleteById(3L);
    }

    @Test
    @WithMockUser(username = "user2")
    void deleteThrowsForUserWithoutDeletePermission() {
        assertThatThrownBy(() -> authorService.deleteById(3L))
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteThrowsForAdminWithoutAclPermission() {
        assertThatThrownBy(() -> authorService.deleteById(3L))
            .isInstanceOf(AccessDeniedException.class);
    }
}
