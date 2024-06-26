package com.thatbackendguy.quizapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.thatbackendguy.quizapp.repository")
public class QuizappApplication
{

    public static void main(String[] args)
    {

        SpringApplication.run(QuizappApplication.class, args);
    }

}
