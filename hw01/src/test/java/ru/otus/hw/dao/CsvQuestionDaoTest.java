package ru.otus.hw.dao;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.reader.CsvQuestionReader;
import ru.otus.hw.dao.reader.CsvQuestionReaderImpl;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

public class CsvQuestionDaoTest {
    private static final String EXISITING_FILE_NAME = "questions.csv";

    private static final int EXISITING_FILE_QUESTIONS_AMOUNT = 2;

    private static final int EXISITING_FILE_ANSWERS_FOR_EACH_QUESTION_AMOUNT = 3;

    private static final String EXISTING_FILE_QUESTION_TEXT = "Question";

    private static final String EXISTING_FILE_ANSWER_TEXT = "Answer";

    private static final String NON_EXISITING_FILE_NAME = "non-existing-file.csv";

    @Test
    public void shouldFaildOnFindAllQuestionsInNotExistingCsvFile() {
        TestFileNameProvider fileNameProvider = mock(TestFileNameProvider.class);
        when(fileNameProvider.getTestFileName()).thenReturn(NON_EXISITING_FILE_NAME);

        CsvQuestionReader csvQuestionReader = new CsvQuestionReaderImpl();

        CsvQuestionDao csvQuestionDao = new CsvQuestionDao(fileNameProvider, csvQuestionReader);

        assertThatThrownBy(() -> csvQuestionDao.findAll())
            .isInstanceOf(QuestionReadException.class);
    }
    
    @Test
    public void shouldFindQuestionsInExistingCsvFile() {
        TestFileNameProvider fileNameProvider = mock(TestFileNameProvider.class);
        when(fileNameProvider.getTestFileName()).thenReturn(EXISITING_FILE_NAME);

        CsvQuestionReader csvQuestionReader = new CsvQuestionReaderImpl();

        CsvQuestionDao csvQuestionDao = new CsvQuestionDao(fileNameProvider, csvQuestionReader);

        List<Question> questions = csvQuestionDao.findAll();

        assertEquals(EXISITING_FILE_QUESTIONS_AMOUNT, questions.size());

        int questionNumber = 1;
        for (Question question : questions) {
            assertEquals(EXISITING_FILE_ANSWERS_FOR_EACH_QUESTION_AMOUNT, question.answers().size());
            assertEquals(String.format("%s %d?", EXISTING_FILE_QUESTION_TEXT, questionNumber), question.text());

            int answerNumber = 1;

            for (Answer answer : question.answers()) {
                assertEquals(String.format("%s %d.%d", EXISTING_FILE_ANSWER_TEXT, questionNumber, answerNumber), answer.text());
                answerNumber++;
            }

            questionNumber++;
        }
    }
}
