package ru.otus.hw.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;

@SpringBootTest
@ContextConfiguration(classes = {TestServiceImpl.class})
public class TestServiceImplTest {

    @MockitoBean
    private LocalizedIOService ioService;

    @MockitoBean
    private QuestionDao questionDao;

    @Autowired
    private TestService testService;

    @ParameterizedTest
    @MethodSource("provideTestCases")
    public void testExecuteTestForStudent(List<Question> questions, List<Integer> choices, Integer rightAnswersCount) {
        when(questionDao.findAll()).thenReturn(questions);
        
        AtomicInteger callCount = new AtomicInteger(0);
        when(ioService.readIntForRangeWithPromptLocalized(anyInt(), anyInt(), anyString(), anyString()))
            .thenAnswer(invocation -> choices.get(callCount.getAndIncrement()));

        var student = mock(Student.class);
        var result = testService.executeTestFor(student);

        assertEquals(rightAnswersCount, result.getRightAnswersCount());
        assertEquals(student, result.getStudent());
        assertEquals(questions, result.getAnsweredQuestions());

        verify(ioService, times(questions.size() * 2 + 2)).printLine(eq(""));
        verify(ioService).printLineLocalized(eq("TestService.answer.the.questions"));

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
        List<Question> questions1 = List.of(
            new Question("Test 1.1",
                List.of(
                    new Answer("Answer 1 to test 1.1", true),
                    new Answer("Answer 2 to test 1.1", false),
                    new Answer("Answer 3 to test 1.1", false)
                )
            ),
            new Question("Test 1.2",
                List.of(
                    new Answer("Answer 1 to test 1.2", false),
                    new Answer("Answer 2 to test 1.2", true),
                    new Answer("Answer 3 to test 1.2", false)
                )
            )
        );
    
        List<Question> questions2 = List.of(
            new Question("Test 2.1",
                List.of(
                    new Answer("Answer 1 to test 2.1", false),
                    new Answer("Answer 2 to test 2.1", false),
                    new Answer("Answer 3 to test 2.1", false),
                    new Answer("Answer 4 to test 2.1", true)
                )
            ),
            new Question("Test 2.2",
                List.of(
                    new Answer("Answer 1 to test 2.2", false),
                    new Answer("Answer 2 to test 2.2", true)
                )
            ),
            new Question("Test 2.3",
                List.of(
                    new Answer("Answer 1 to test 2.3", false),
                    new Answer("Answer 2 to test 2.3", false),
                    new Answer("Answer 3 to test 2.3", false),
                    new Answer("Answer 4 to test 2.3", true),
                    new Answer("Answer 5 to test 2.3", false)
                )
            )
        );

        return Stream.of(
            Arguments.of(questions1, List.of(1, 2), 2),
            Arguments.of(questions1, List.of(2, 1), 0),        
            Arguments.of(questions1, List.of(1, 1), 1),
            Arguments.of(questions2, List.of(4, 2, 4), 3),
            Arguments.of(questions2, List.of(2, 1, 3), 0),
            Arguments.of(questions2, List.of(4, 1, 4), 2),
            Arguments.of(questions2, List.of(1, 2, 5), 1)
        );
    }
}
