package com.thatbackendguy.quizapp.controller;

import com.thatbackendguy.quizapp.dto.ErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController implements ErrorController
{

    @RequestMapping("/error")
    public ResponseEntity<ErrorDTO> handleError(HttpServletRequest request)
    {

        var exception = (Exception) request.getAttribute("javax.servlet.error.exception");

        var errorResponse = new ErrorDTO();

        errorResponse.setMessage(exception != null ? exception.getMessage() : "Unexpected error");

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

}