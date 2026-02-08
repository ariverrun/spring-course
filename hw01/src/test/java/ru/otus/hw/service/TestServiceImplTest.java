package ru.otus.hw.service;

import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.stream.Stream;

public class TestServiceImplTest {
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
            
            for (Answer answer : question.answers()) {
                verify(ioService).printLine(eq(" - " + answer.text()));
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
                            new Answer("Answer 3 to test 1", false)
                        )
                    ),
                    new Question(
                        "Test 2", 
                        List.of(
                            new Answer("Answer 1 to test 2", false),
                            new Answer("Answer 2 to test 2", true),
                            new Answer("Answer 3 to test 2", false)
                        )
                    )
                )
            ),
            Arguments.of(
                List.of(
                    new Question(
                        "Test 1", 
                        List.of(
                            new Answer("Answer 1 to test 1", true),
                            new Answer("Answer 2 to test 1", false),
                            new Answer("Answer 3 to test 1", false)
                        )
                    ),
                    new Question(
                        "Test 2", 
                        List.of(
                            new Answer("Answer 1 to test 2", false),
                            new Answer("Answer 2 to test 2", true),
                            new Answer("Answer 3 to test 2", false)
                        )
                    )
                )
            )                 
        );
    }
}
