package ru.otus.hw.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import ru.otus.hw.dto.UpdateGenreRequestDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class GenreServiceAclTest {

    @Autowired
    private GenreService genreService;

    @Test
    @WithMockUser(username = "user1")
    void findAllReturnsAllGenresForUserWithReadOnAll() {
        assertThat(genreService.findAll()).hasSize(6);
    }

    @Test
    @WithMockUser(username = "user2")
    void findAllReturnsAllGenresForReadOnlyUser() {
        assertThat(genreService.findAll()).hasSize(6);
    }

    @Test
    @WithMockUser(username = "user3")
    void findAllReturnsAllGenresForAnyUser() {
        assertThat(genreService.findAll()).hasSize(6);
    }

    @Test
    @WithMockUser(username = "user1")
    void findByIdSucceedsForUserWithReadPermission() {
        var result = genreService.findById(1L);
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    @WithMockUser(username = "user2")
    void findByIdSucceedsForReadOnlyUser() {
        assertThat(genreService.findById(1L)).isNotNull();
    }

    @Test
    @WithMockUser(username = "user3")
    void findByIdSucceedsForAnyUser() {
        assertThat(genreService.findById(1L)).isNotNull();
    }

    @Test
    @WithMockUser(username = "user1")
    void updateSucceedsForUserWithWritePermission() {
        var dto = new UpdateGenreRequestDto("Genre_1.1");
        var result = genreService.update(1L, dto);
        assertThat(result.name()).isEqualTo("Genre_1.1");
    }

    @Test
    @WithMockUser(username = "user2")
    void updateThrowsForUserWithoutWritePermission() {
        var dto = new UpdateGenreRequestDto("Genre_1.2");
        assertThatThrownBy(() -> genreService.update(1L, dto))
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateThrowsForAdminWithoutAclPermission() {
        var dto = new UpdateGenreRequestDto("Genre_1.3");
        assertThatThrownBy(() -> genreService.update(1L, dto))
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(username = "user1")
    void deleteSucceedsForUserWithDeletePermission() {
        genreService.deleteById(6L);
    }

    @Test
    @WithMockUser(username = "user2")
    void deleteThrowsForUserWithoutDeletePermission() {
        assertThatThrownBy(() -> genreService.deleteById(6L))
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteThrowsForAdminWithoutAclPermission() {
        assertThatThrownBy(() -> genreService.deleteById(6L))
            .isInstanceOf(AccessDeniedException.class);
    }
}
