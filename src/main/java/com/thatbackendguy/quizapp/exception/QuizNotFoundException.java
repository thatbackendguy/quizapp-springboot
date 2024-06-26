package com.thatbackendguy.quizapp.exception;

public class QuizNotFoundException extends RuntimeException
{
    public QuizNotFoundException()
    {

        super("No quizzes found");
    }

    public QuizNotFoundException(Long id)
    {

        super("Quiz with id " + id + " not found");
    }
    public QuizNotFoundException(String name)
    {

        super("Quiz not found for department: " + name);
    }

}
