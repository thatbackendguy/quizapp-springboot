package com.thatbackendguy.quizapp.exception;

public class BadRequestException extends RuntimeException
{

    public BadRequestException()
    {

        super("Bad request! Invalid body");
    }

}
