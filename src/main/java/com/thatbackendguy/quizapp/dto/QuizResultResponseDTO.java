package com.thatbackendguy.quizapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultResponseDTO
{
    private float score;
    private List<QuizSubmit> results;

}