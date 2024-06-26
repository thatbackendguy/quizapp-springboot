package com.thatbackendguy.quizapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "quizzes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizEntity
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false, columnDefinition = "JSON")
    @Convert(converter = StringListConverter.class)
    private List<String> options;

    @Column(nullable = false)
    private String answer;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private DepartmentEntity department;

}