package ru.otus.hw.dao.reader;

import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

public interface CsvQuestionReader {
    List<QuestionDto> readFromResourceFile(String fileName) throws QuestionReadException;
}