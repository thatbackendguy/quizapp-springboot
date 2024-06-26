package com.thatbackendguy.quizapp.controller;

import com.thatbackendguy.quizapp.dto.QuizDTO;
import com.thatbackendguy.quizapp.dto.QuizResponseDTO;
import com.thatbackendguy.quizapp.exception.BadRequestException;
import com.thatbackendguy.quizapp.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController
{

    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService)
    {

        this.quizService = quizService;
    }

    @GetMapping
    public ResponseEntity<List<QuizResponseDTO>> getQuiz(@RequestBody(required = false) QuizDTO quizDTO)
    {

        var quizzes = quizService.getQuiz(quizDTO);

        return ResponseEntity.ok(quizzes);
    }

    @PostMapping
    public ResponseEntity<QuizDTO> createQuiz(@RequestBody QuizDTO quizDTO)
    {

        if (quizDTO.getQuestion().isEmpty())
        {
            throw new BadRequestException("Question is required");
        }
        else if (quizDTO.getAnswer().isEmpty())
        {
            throw new BadRequestException("Answer is required");
        }
        else if (quizDTO.getOptions().isEmpty())
        {
            throw new BadRequestException("Options are required");
        }
        else if (quizDTO.getDepartment().getId() <= 0)
        {
            throw new BadRequestException("Valid department ID is required");
        }
        else if (quizDTO.getDepartment().getName().isEmpty())
        {
            throw new BadRequestException("Department name is required");
        }

        var createdQuiz = quizService.createQuiz(quizDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuiz);
    }

    @PutMapping
    public ResponseEntity<QuizDTO> updateQuiz(@RequestBody QuizDTO quizDTO)
    {

        if (quizDTO.getId() == null)
        {
            throw new BadRequestException("Quiz ID is required");
        }
        else if (quizDTO.getId() <= 0)
        {
            throw new BadRequestException("Quiz ID must be a positive number");
        }
        else if (quizDTO.getDepartment() == null)
        {
            throw new BadRequestException("Department is required");
        }
        else if (quizDTO.getDepartment().getId() <= 0)
        {
            throw new BadRequestException("Valid department ID is required");
        }
        else if (quizDTO.getOptions().isEmpty())
        {
            throw new BadRequestException("Options are required");
        }
        else if (quizDTO.getAnswer().isEmpty())
        {
            throw new BadRequestException("Answer is required");
        }
        else if (quizDTO.getQuestion().isEmpty())
        {
            throw new BadRequestException("Question is required");
        }

        var updatedQuiz = quizService.updateQuiz(quizDTO.getId(), quizDTO);

        return ResponseEntity.ok(updatedQuiz);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteQuiz(@RequestBody QuizDTO quizDTO)
    {

        if (quizDTO.getId() == null)
        {
            throw new BadRequestException("Quiz ID is required");
        }
        else if (quizDTO.getId() <= 0)
        {
            throw new BadRequestException("Quiz ID must be a positive number");
        }

        quizService.deleteQuiz(quizDTO.getId());

        return ResponseEntity.noContent().build();
    }

}
