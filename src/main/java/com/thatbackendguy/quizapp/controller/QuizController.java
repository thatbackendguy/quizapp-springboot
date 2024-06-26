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

        if (quizDTO.getQuestion().isEmpty() || quizDTO.getAnswer().isEmpty() || quizDTO.getOptions()
                .isEmpty() || quizDTO.getDepartment().getId() <= 0 || quizDTO.getDepartment().getName().isEmpty())
            throw new BadRequestException();

        var createdQuiz = quizService.createQuiz(quizDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuiz);
    }

    @PutMapping
    public ResponseEntity<QuizDTO> updateQuiz(@RequestBody QuizDTO quizDTO)
    {

        if (quizDTO.getId() == null || quizDTO.getId() <= 0 || quizDTO.getDepartment() == null || quizDTO.getDepartment()
                .getId() <= 0 || quizDTO.getOptions().isEmpty() || quizDTO.getAnswer()
                .isEmpty() || quizDTO.getQuestion().isEmpty()) throw new BadRequestException();

        var updatedQuiz = quizService.updateQuiz(quizDTO.getId(), quizDTO);

        return ResponseEntity.ok(updatedQuiz);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteQuiz(@RequestBody QuizDTO quizDTO)
    {

        if (quizDTO.getId() == null || quizDTO.getId() <= 0) throw new BadRequestException();

        quizService.deleteQuiz(quizDTO.getId());

        return ResponseEntity.noContent().build();
    }

}
