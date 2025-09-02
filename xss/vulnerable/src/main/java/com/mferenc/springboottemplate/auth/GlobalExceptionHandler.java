package com.mferenc.springboottemplate.auth;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound() {
        return "redirect:/tickets";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException() {
        return "redirect:/tickets";
    }
}