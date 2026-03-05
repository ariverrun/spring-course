package ru.otus.hw.shell;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.service.TestRunnerService;

@ShellComponent
@RequiredArgsConstructor
public class RunTestCommand {

    private final TestRunnerService testRunnerService;
    
    @ShellMethod(value = "Runs student test", key = {"rt", "run-test"})
    public void runTest() {
        testRunnerService.run();
    }

}
