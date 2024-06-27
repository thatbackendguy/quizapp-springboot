package com.thatbackendguy.quizapp.exception;

public class InvalidCredentialsException extends RuntimeException
{

    public InvalidCredentialsException()
    {
        super("Invalid credentials");
    }

}
