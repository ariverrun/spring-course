package ru.otus.hw.dao;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.dao.reader.CsvQuestionReader;
import ru.otus.hw.domain.Question;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    private final CsvQuestionReader csvQuestionReader;

    @Override
    public List<Question> findAll() {
        return csvQuestionReader
                .readFromResourceFile(fileNameProvider.getTestFileName())
                .stream()
                .map(QuestionDto::toDomainObject)
                .collect(Collectors.toList());
    }
}
