package ru.otus.hw.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class DefaultAdvice {
 
    @ExceptionHandler({EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleEntityNotFound(Exception ex) {
        log.error("Entity not found error", ex);
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("errorMessage", "Объект не найден");
        return mav;
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleAddressNotFound(Exception ex) {
        log.error("Handler or resource not found error", ex);
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("errorMessage", "Адрес не существует");
        return mav;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ModelAndView handleInvalidArguments(MethodArgumentNotValidException ex) {
        log.error("Invalid arguments error", ex);
        ModelAndView mav = new ModelAndView("error/400");
        mav.addObject("errorMessage", "Невалидные данные");
        return mav;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGenericError(Exception ex) {
        log.error("Internal server error", ex);
        ModelAndView mav = new ModelAndView("error/500");
        mav.addObject("errorMessage", "Ошибка");
        return mav;
    }
}
