package com.thatbackendguy.quizapp.exception;

public class UserNotFoundException extends RuntimeException
{
    public UserNotFoundException()
    {

        super("No users found");
    }

    public UserNotFoundException(Long id)
    {

        super("User with id " + id + " not found");
    }

}
