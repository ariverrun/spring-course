package ru.otus.hw.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class DefaultAdvice {
 
    @ExceptionHandler({EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleEntityNotFound(Exception ex) {
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("errorMessage", "Объект не найден");
        return mav;
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleAddressNotFound(Exception ex) {
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("errorMessage", "Адрес не существует");
        return mav;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGenericError(Exception ex) {
        ModelAndView mav = new ModelAndView("error/500");
        mav.addObject("errorMessage", "Ошибка");
        return mav;
    }
}
