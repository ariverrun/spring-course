package ru.otus.hw.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

public class TestServiceImplTest {

    @Test
    void shouldPrintErrorMessageWhenQuestionDaoThrowsException() {
        IOService ioService = mock(IOService.class);
        QuestionDao questionDao = mock(QuestionDao.class);

        String errorMessage = "Some exception on trying to find all questions";

        when(questionDao.findAll()).thenThrow(new RuntimeException(errorMessage));

        TestService testService = new TestServiceImpl(ioService, questionDao);
        testService.executeTest();

        verify(ioService).printLine(eq(""));
        verify(ioService).printFormattedLine(eq("Please answer the questions below%n"));
        verify(ioService).printFormattedLine(
            eq("Failed to get questions list: %s"),
            eq(errorMessage)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    public void testExecuteTest(List<Question> questions) {
        IOService ioService = mock(IOService.class);
        QuestionDao questionDao = mock(QuestionDao.class);
        when(questionDao.findAll()).thenReturn(questions);

        TestService testService = new TestServiceImpl(ioService, questionDao);
        testService.executeTest();

        verify(ioService, times(questions.size() * 2 + 1)).printLine(eq(""));
        verify(ioService).printFormattedLine(eq("Please answer the questions below%n"));

        for (Question question : questions) {
            verify(ioService).printLine(eq(question.text()));
            
            int answerNumber = 1;
            for (Answer answer : question.answers()) {
                verify(ioService).printFormattedLine(
                    eq("%d. %s"),
                    eq(answerNumber),
                    eq(answer.text())
                );
                answerNumber++;
            }
        }
    }

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                new Question(
                                        "Test 1",
                                        List.of(
                                                new Answer("Answer 1 to test 1", true),
                                                new Answer("Answer 2 to test 1", false),
                                                new Answer("Answer 3 to test 1", false))),
                                new Question(
                                        "Test 2",
                                        List.of(
                                                new Answer("Answer 1 to test 2", false),
                                                new Answer("Answer 2 to test 2", true),
                                                new Answer("Answer 3 to test 2", false))))),
                Arguments.of(
                        List.of(
                                new Question(
                                        "Test 1",
                                        List.of(
                                                new Answer("Answer 1 to test 1", true),
                                                new Answer("Answer 2 to test 1", false),
                                                new Answer("Answer 3 to test 1", false))),
                                new Question(
                                        "Test 2",
                                        List.of(
                                                new Answer("Answer 1 to test 2", false),
                                                new Answer("Answer 2 to test 2", true),
                                                new Answer("Answer 3 to test 2", false))))));
    }
}
