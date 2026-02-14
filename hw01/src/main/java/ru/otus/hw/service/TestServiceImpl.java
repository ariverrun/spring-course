package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        try {
            ioService.printLine("");
            ioService.printFormattedLine("Please answer the questions below%n");

            for (Question question : questionDao.findAll()) {
                printQuestion(question);
            }            
        } catch (Exception e) {
            System.out.println(e);
            ioService.printFormattedLine("Failed to get questions list: %s", e.getMessage());
        }
    }

    private void printQuestion(Question question) {
        ioService.printLine(question.text());
        ioService.printLine("");

        int answerNumber = 1;
        for (Answer answer : question.answers()) {
            ioService.printFormattedLine("%d. %s", answerNumber, answer.text());
            answerNumber++;
        }

        ioService.printLine("");
    }
}
