package ru.otus.hw.batch;

import java.util.List;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.models.document.AuthorDocument;
import ru.otus.hw.models.document.BookDocument;
import ru.otus.hw.models.document.CommentDocument;
import ru.otus.hw.models.document.GenreDocument;
import ru.otus.hw.models.jpa.Author;
import ru.otus.hw.models.jpa.Book;
import ru.otus.hw.models.jpa.Comment;
import ru.otus.hw.models.jpa.Genre;
import ru.otus.hw.repositories.document.AuthorDocumentRepository;
import ru.otus.hw.repositories.document.BookDocumentRepository;
import ru.otus.hw.repositories.document.CommentDocumentRepository;
import ru.otus.hw.repositories.document.GenreDocumentRepository;
import ru.otus.hw.repositories.jpa.AuthorRepository;
import ru.otus.hw.repositories.jpa.BookRepository;
import ru.otus.hw.repositories.jpa.CommentRepository;
import ru.otus.hw.repositories.jpa.GenreRepository;

@Configuration
@RequiredArgsConstructor
public class JobConfig {

    private record AuthorItem(long jpaId, AuthorDocument authorDocument) {}

    private record GenreItem(long jpaId, GenreDocument genreDocument) {}

    private record BookItem(long jpaId, BookDocument bookDocument) {}

    private final AuthorRepository jpaAuthorRepository;

    private final GenreRepository jpaGenreRepository;

    private final BookRepository jpaBookRepository;

    private final CommentRepository jpaCommentRepository;

    private final AuthorDocumentRepository authorDocumentRepository;

    private final GenreDocumentRepository genreDocumentRepository;

    private final BookDocumentRepository bookDocumentRepository;

    private final CommentDocumentRepository commentDocumentRepository;

    private final MigrationIdCache cache;

    @Bean
    public Step cleanMongoStep(JobRepository jobRepository, PlatformTransactionManager txManager) {
        return new StepBuilder("cleanMongoStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    commentDocumentRepository.deleteAll();
                    bookDocumentRepository.deleteAll();
                    genreDocumentRepository.deleteAll();
                    authorDocumentRepository.deleteAll();
                    cache.clear();
                    return RepeatStatus.FINISHED;
                }, txManager)
                .build();
    }

    @Bean
    @StepScope
    public ListItemReader<Author> authorItemReader() {
        return new ListItemReader<>(jpaAuthorRepository.findAll());
    }

    @Bean
    @StepScope
    public ListItemReader<Genre> genreItemReader() {
        return new ListItemReader<>(jpaGenreRepository.findAll());
    }

    @Bean
    @StepScope
    public ListItemReader<Book> bookItemReader() {
        return new ListItemReader<>(jpaBookRepository.findAll());
    }

    @Bean
    @StepScope
    public ListItemReader<Comment> commentItemReader() {
        return new ListItemReader<>(jpaCommentRepository.findAllWithBook());
    }

    @Bean
    public Step migrateAuthorsStep(JobRepository jobRepository, PlatformTransactionManager txManager,
                                   ListItemReader<Author> authorItemReader) {
        return new StepBuilder("migrateAuthorsStep", jobRepository)
                .<Author, AuthorItem>chunk(10, txManager)
                .reader(authorItemReader)
                .processor(author -> new AuthorItem(author.getId(),
                        new AuthorDocument(null, author.getFullName())))
                .writer(chunk -> {
                    for (AuthorItem item : chunk.getItems()) {
                        AuthorDocument saved = authorDocumentRepository.save(item.authorDocument());
                        cache.putAuthorId(item.jpaId(), saved.getId());
                    }
                })
                .build();
    }

    @Bean
    public Step migrateGenresStep(JobRepository jobRepository, PlatformTransactionManager txManager,
                                  ListItemReader<Genre> genreItemReader) {
        return new StepBuilder("migrateGenresStep", jobRepository)
                .<Genre, GenreItem>chunk(10, txManager)
                .reader(genreItemReader)
                .processor(genre -> new GenreItem(genre.getId(),
                        new GenreDocument(null, genre.getName())))
                .writer(chunk -> {
                    for (GenreItem item : chunk.getItems()) {
                        GenreDocument saved = genreDocumentRepository.save(item.genreDocument());
                        cache.putGenreId(item.jpaId(), saved.getId());
                    }
                })
                .build();
    }

    @Bean
    public Step migrateBooksStep(JobRepository jobRepository, PlatformTransactionManager txManager,
                                 ListItemReader<Book> bookItemReader) {
        return new StepBuilder("migrateBooksStep", jobRepository)
                .<Book, BookItem>chunk(10, txManager)
                .reader(bookItemReader)
                .processor(book -> {
                    AuthorDocument authorDocument = authorDocumentRepository
                            .findById(cache.getAuthorId(book.getAuthor().getId()))
                            .orElseThrow();
                    List<GenreDocument> genreDocuments = book.getGenres().stream()
                            .map(g -> genreDocumentRepository
                                    .findById(cache.getGenreId(g.getId()))
                                    .orElseThrow())
                            .toList();
                    return new BookItem(book.getId(),
                            new BookDocument(null, book.getTitle(), authorDocument, genreDocuments));
                })
                .writer(chunk -> {
                    for (BookItem item : chunk.getItems()) {
                        BookDocument saved = bookDocumentRepository.save(item.bookDocument());
                        cache.putBookId(item.jpaId(), saved.getId());
                    }
                })
                .build();
    }

    @Bean
    public Step migrateCommentsStep(JobRepository jobRepository, PlatformTransactionManager txManager,
                                    ListItemReader<Comment> commentItemReader) {
        return new StepBuilder("migrateCommentsStep", jobRepository)
                .<Comment, CommentDocument>chunk(10, txManager)
                .reader(commentItemReader)
                .processor(comment -> {
                    BookDocument bookDocument = bookDocumentRepository
                            .findById(cache.getBookId(comment.getBook().getId()))
                            .orElseThrow();
                    return new CommentDocument(null, bookDocument, comment.getText());
                })
                .writer(chunk -> commentDocumentRepository.saveAll(chunk.getItems()))
                .build();
    }
}
