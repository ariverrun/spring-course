package ru.otus.hw.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ServerWebInputException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.RestApiErrorDto;
import ru.otus.hw.dto.RestApiValidationErrorDto;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<RestApiErrorDto> handleNotFound(EntityNotFoundException ex) {
        return Mono.just(new RestApiErrorDto("NOT_FOUND", "Resource not found: " + ex.getMessage()));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<RestApiValidationErrorDto> handleInvalidArguments(WebExchangeBindException ex) {
        Map<String, String> violations = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            violations.put(fieldName, errorMessage);
        });

        return Mono.just(new RestApiValidationErrorDto("VALIDATION_FAILED", violations));
    }

    @ExceptionHandler(ServerWebInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<RestApiErrorDto> handleMissingQueryParam(ServerWebInputException ex) {
        return Mono.just(new RestApiErrorDto("MISSING_QUERY_PARAM", "Missing query parameter: " + ex.getMessage()));
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<RestApiErrorDto> handleMethodNotAllowed(MethodNotAllowedException ex) {
        return Mono.just(new RestApiErrorDto("NOT_FOUND", "Resource not found: " + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<RestApiErrorDto> handleInternalError(Exception ex) {
        log.error("Internal server error", ex);
        return Mono.just(new RestApiErrorDto("INTERNAL_ERROR", "Internal server error"));
    }
}