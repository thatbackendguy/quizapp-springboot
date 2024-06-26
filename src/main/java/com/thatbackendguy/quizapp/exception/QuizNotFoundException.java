package com.thatbackendguy.quizapp.exception;

public class QuizNotFoundException extends RuntimeException
{

    public QuizNotFoundException(Long id)
    {

        super("Quiz with id " + id + " not found");
    }

}
