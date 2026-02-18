package ru.otus.hw.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@RequiredArgsConstructor
@Service
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question: questions) {
            var isAnswerValid = askQuestion(question);
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private Boolean askQuestion(Question question) {
        ioService.printLine(question.text());
        ioService.printLine("");

        int answerNumber = 1;
        for (Answer answer : question.answers()) {
            ioService.printFormattedLine("%d. %s", answerNumber, answer.text());
            answerNumber++;
        }

        ioService.printLine("");

        int choiceNumber = ioService.readIntForRangeWithPrompt(1, answerNumber, "Enter your choice", "Invalid choice");

        return isAnswerCorrectForQuestion(choiceNumber, question);
    }

    private Boolean isAnswerCorrectForQuestion(int choiceNumber, Question question) {
        var answer = question.answers().get(choiceNumber - 1);

        if (answer.isCorrect()) {
            return true;
        }

        return false;
    }
}
