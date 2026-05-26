package ru.otus.hw.batch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.otus.hw.repositories.document.AuthorDocumentRepository;
import ru.otus.hw.repositories.document.BookDocumentRepository;
import ru.otus.hw.repositories.document.CommentDocumentRepository;
import ru.otus.hw.repositories.document.GenreDocumentRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@SpringBatchTest
class MigrationJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private Job migrationJob;

    @Autowired
    private AuthorDocumentRepository authorDocumentRepository;

    @Autowired
    private GenreDocumentRepository genreDocumentRepository;

    @Autowired
    private BookDocumentRepository bookDocumentRepository;

    @Autowired
    private CommentDocumentRepository commentDocumentRepository;

    @BeforeEach
    void setUp() {
        jobRepositoryTestUtils.removeJobExecutions();
        jobLauncherTestUtils.setJob(migrationJob);
    }

    @Test
    void shouldFinishWithCompletedStatus() throws Exception {
        JobExecution execution = jobLauncherTestUtils.launchJob();
        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    @Test
    void shouldMigrateAllAuthorsToMongo() throws Exception {
        jobLauncherTestUtils.launchJob();
        assertThat(authorDocumentRepository.findAll()).hasSize(3);
    }

    @Test
    void shouldMigrateAllGenresToMongo() throws Exception {
        jobLauncherTestUtils.launchJob();
        assertThat(genreDocumentRepository.findAll()).hasSize(6);
    }

    @Test
    void shouldMigrateAllBooksToMongo() throws Exception {
        jobLauncherTestUtils.launchJob();
        assertThat(bookDocumentRepository.findAll()).hasSize(3);
    }

    @Test
    void shouldMigrateAllCommentsToMongo() throws Exception {
        jobLauncherTestUtils.launchJob();
        assertThat(commentDocumentRepository.findAll()).hasSize(4);
    }

    @Test
    void shouldEmbedAuthorAndGenresIntoMigratedBook() throws Exception {
        jobLauncherTestUtils.launchJob();

        var books = bookDocumentRepository.findAll();
        assertThat(books).allSatisfy(book -> {
            assertThat(book.getAuthor()).isNotNull();
            assertThat(book.getAuthor().getFullName()).isNotBlank();
            assertThat(book.getGenres()).hasSize(2);
        });
    }

    @Test
    void shouldEmbedBookIntoMigratedComment() throws Exception {
        jobLauncherTestUtils.launchJob();

        var comments = commentDocumentRepository.findAll();
        assertThat(comments).allSatisfy(comment -> {
            assertThat(comment.getBook()).isNotNull();
            assertThat(comment.getBook().getId()).isNotBlank();
            assertThat(comment.getBook().getTitle()).isNotBlank();
        });
    }

    @Test
    void shouldProduceSameDataCountWhenRunTwice() throws Exception {
        jobLauncherTestUtils.launchJob();
        jobRepositoryTestUtils.removeJobExecutions();
        jobLauncherTestUtils.launchJob();

        assertThat(authorDocumentRepository.findAll()).hasSize(3);
        assertThat(genreDocumentRepository.findAll()).hasSize(6);
        assertThat(bookDocumentRepository.findAll()).hasSize(3);
        assertThat(commentDocumentRepository.findAll()).hasSize(4);
    }
}
