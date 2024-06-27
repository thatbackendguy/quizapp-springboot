package com.thatbackendguy.quizapp.exception;

import com.thatbackendguy.quizapp.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalRuntimeExceptionHandler
{

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDTO> handleGenericException(Exception ex)
    {
        var error = new ErrorDTO();

        error.setMessage(ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

}