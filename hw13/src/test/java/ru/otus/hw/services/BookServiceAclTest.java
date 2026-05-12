package ru.otus.hw.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import ru.otus.hw.dto.UpdateBookRequestDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class BookServiceAclTest {

    @Autowired
    private BookService bookService;

    @Test
    @WithMockUser(username = "user1")
    void findAllReturnsAllBooksForUserWithReadOnAll() {
        assertThat(bookService.findAll()).hasSize(3);
    }

    @Test
    @WithMockUser(username = "user2")
    void findAllReturnsAllBooksForReadOnlyUser() {
        assertThat(bookService.findAll()).hasSize(3);
    }

    @Test
    @WithMockUser(username = "user3")
    void findAllReturnsAllBooksForAnyUser() {
        assertThat(bookService.findAll()).hasSize(3);
    }

    @Test
    @WithMockUser(username = "user1")
    void findByIdSucceedsForUserWithReadPermission() {
        var result = bookService.findById(1L);
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    @WithMockUser(username = "user2")
    void findByIdSucceedsForReadOnlyUser() {
        assertThat(bookService.findById(1L)).isNotNull();
    }

    @Test
    @WithMockUser(username = "user3")
    void findByIdSucceedsForAnyUser() {
        assertThat(bookService.findById(1L)).isNotNull();
    }

    @Test
    @WithMockUser(username = "user1")
    void updateSucceedsForUserWithWritePermission() {
        var dto = new UpdateBookRequestDto("Title_1.1", 1L, Set.of(1L, 2L));
        var result = bookService.update(1L, dto);
        assertThat(result.title()).isEqualTo("Title_1.1");
    }

    @Test
    @WithMockUser(username = "user2")
    void updateThrowsForUserWithoutWritePermission() {
        var dto = new UpdateBookRequestDto("Title_1.2", 1L, Set.of(1L, 2L));
        assertThatThrownBy(() -> bookService.update(1L, dto))
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateThrowsForAdminWithoutAclPermission() {
        var dto = new UpdateBookRequestDto("Title_1.3", 1L, Set.of(1L, 2L));
        assertThatThrownBy(() -> bookService.update(1L, dto))
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(username = "user1")
    void deleteSucceedsForUserWithDeletePermission() {
        bookService.deleteById(3L);
    }

    @Test
    @WithMockUser(username = "user2")
    void deleteThrowsForUserWithoutDeletePermission() {
        assertThatThrownBy(() -> bookService.deleteById(3L))
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteThrowsForAdminWithoutAclPermission() {
        assertThatThrownBy(() -> bookService.deleteById(3L))
            .isInstanceOf(AccessDeniedException.class);
    }
}
