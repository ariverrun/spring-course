package ru.otus.hw.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MigrationJobConfig {

    private final JobRepository jobRepository;

    private final Step cleanMongoStep;

    private final Step migrateAuthorsStep;

    private final Step migrateGenresStep;

    private final Step migrateBooksStep;

    private final Step migrateCommentsStep;

    @Bean
    public Job migrationJob() {
        return new JobBuilder("migrationJob", jobRepository)
                .start(cleanMongoStep)
                .next(migrateAuthorsStep)
                .next(migrateGenresStep)
                .next(migrateBooksStep)
                .next(migrateCommentsStep)
                .build();
    }
}
