package com.thatbackendguy.quizapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizDTO
{

    private Long id;

    private String question;

    private List<String> options;

    private String answer;

    private DepartmentDTO department;

    private String deptName;

}