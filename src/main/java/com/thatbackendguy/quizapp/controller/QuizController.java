package com.thatbackendguy.quizapp.controller;

import com.thatbackendguy.quizapp.dto.QuizDTO;
import com.thatbackendguy.quizapp.dto.QuizResponseDTO;
import com.thatbackendguy.quizapp.service.QuizService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<List<QuizResponseDTO>> getQuiz(@RequestBody(required = false) QuizDTO quizDTO, HttpServletRequest request)
    {

        var quizzes = quizService.getQuiz(quizDTO, request);

        return ResponseEntity.ok(quizzes);
    }

    @PostMapping
    public ResponseEntity<QuizDTO> createQuiz(@RequestBody QuizDTO quizDTO, HttpServletRequest request)
    {


        var createdQuiz = quizService.createQuiz(quizDTO, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuiz);
    }

    @PutMapping
    public ResponseEntity<QuizDTO> updateQuiz(@RequestBody QuizDTO quizDTO, HttpServletRequest request)
    {

        var updatedQuiz = quizService.updateQuiz(quizDTO, request);

        return ResponseEntity.ok(updatedQuiz);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteQuiz(@RequestBody QuizDTO quizDTO, HttpServletRequest request)
    {

        quizService.deleteQuiz(quizDTO, request);

        return ResponseEntity.noContent().build();
    }


}
