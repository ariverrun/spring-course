package ru.otus.hw.actuator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "logfiles")
public class LogFilesEndpoint {

    private static final Path LOGS_DIR = Path.of("logs");

    @ReadOperation
    public List<String> listFiles() throws IOException {
        if (!Files.exists(LOGS_DIR)) {
            return List.of();
        }
        try (Stream<Path> files = Files.list(LOGS_DIR)) {
            return files
                    .filter(p -> p.toString().endsWith(".log"))
                    .map(p -> p.getFileName().toString())
                    .sorted()
                    .toList();
        }
    }

    @ReadOperation
    public String readFile(@Selector String filename) throws IOException {
        Path filePath = LOGS_DIR.resolve(filename).normalize();
        if (!filePath.startsWith(LOGS_DIR)) {
            return "Access denied";
        }
        if (!Files.exists(filePath)) {
            return "File not found: " + filename;
        }
        return Files.readString(filePath);
    }
}
