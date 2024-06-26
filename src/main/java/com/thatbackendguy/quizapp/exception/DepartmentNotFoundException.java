package com.thatbackendguy.quizapp.exception;

public class DepartmentNotFoundException extends RuntimeException
{

    public DepartmentNotFoundException()
    {

        super("No departments found");
    }

    public DepartmentNotFoundException(Long id)
    {

        super("Could not find department " + id);
    }

    public DepartmentNotFoundException(String name)
    {

        super("Could not find department " + name);
    }

}
