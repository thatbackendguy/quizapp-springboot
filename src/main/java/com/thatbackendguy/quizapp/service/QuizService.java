package com.thatbackendguy.quizapp.service;

import com.thatbackendguy.quizapp.dto.QuizDTO;
import com.thatbackendguy.quizapp.dto.QuizResponseDTO;
import com.thatbackendguy.quizapp.entity.DepartmentEntity;
import com.thatbackendguy.quizapp.entity.QuizEntity;
import com.thatbackendguy.quizapp.exception.BadRequestException;
import com.thatbackendguy.quizapp.exception.DepartmentNotFoundException;
import com.thatbackendguy.quizapp.exception.QuizNotFoundException;
import com.thatbackendguy.quizapp.repository.DepartmentRepository;
import com.thatbackendguy.quizapp.repository.QuizRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService
{

    private final QuizRepository quizRepository;

    private final DepartmentRepository departmentRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public QuizService(QuizRepository quizRepository, DepartmentRepository departmentRepository, ModelMapper modelMapper)
    {

        this.quizRepository = quizRepository;
        this.departmentRepository = departmentRepository;
        this.modelMapper = modelMapper;
    }

    public List<QuizResponseDTO> getQuiz(QuizDTO quizDTO)
    {

        List<QuizEntity> quizzes;

        if (quizDTO == null || quizDTO.getId() == null)
        {
            quizzes = quizRepository.findAll();
        }
        else if (quizDTO.getId() > 0)
        {
            quizzes = quizRepository.findById(quizDTO.getId())
                    .map(Collections::singletonList)
                    .orElseThrow(() -> new QuizNotFoundException(quizDTO.getId()));
        }
        else if (quizDTO.getDeptName() != null && !quizDTO.getDeptName().isEmpty())
        {
            var department = departmentRepository.findByName(quizDTO.getDeptName());
            if (department == null)
            {
                throw new DepartmentNotFoundException(quizDTO.getDeptName());
            }
            quizzes = quizRepository.findByDeptId(department.getId());
        }
        else
        {
            quizzes = quizRepository.findAll();
        }

        if (quizzes.isEmpty())
        {
            throw new QuizNotFoundException();
        }

        return quizzes.stream()
                .map(quizEntity -> modelMapper.map(quizEntity, QuizResponseDTO.class))
                .collect(Collectors.toList());
    }

    public QuizDTO createQuiz(QuizDTO quizDTO)
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

        var quizEntity = modelMapper.map(quizDTO, QuizEntity.class);

        if (quizEntity.getDepartment() != null && quizEntity.getDepartment().getId() != null)
        {
            DepartmentEntity departmentEntity = departmentRepository.findById(quizEntity.getDepartment().getId())
                    .orElseThrow(() -> new DepartmentNotFoundException(quizEntity.getDepartment().getId()));
            quizEntity.setDepartment(departmentEntity);
        }

        var quiz = quizRepository.save(quizEntity);

        return modelMapper.map(quiz, QuizDTO.class);
    }

    public QuizDTO updateQuiz(QuizDTO quizDTO)
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

        var quizEntity = quizRepository.findById(quizDTO.getId()).map(quiz ->
        {
            quiz.setQuestion(quizDTO.getQuestion());
            quiz.setOptions(quizDTO.getOptions());
            quiz.setAnswer(quizDTO.getAnswer());
            if (quizDTO.getDepartment() != null && quizDTO.getDepartment().getId() != null)
            {
                var department = departmentRepository.findById(quizDTO.getDepartment().getId())
                        .orElseThrow(() -> new DepartmentNotFoundException(quizDTO.getDepartment().getId()));

                quiz.setDepartment(department);
            }
            return quizRepository.save(quiz);
        }).orElseThrow(() -> new QuizNotFoundException(quizDTO.getId()));

        return modelMapper.map(quizEntity, QuizDTO.class);
    }

    public void deleteQuiz(QuizDTO quizDTO)
    {

        if (quizDTO.getId() == null)
        {
            throw new BadRequestException("Quiz ID is required");
        }
        else if (quizDTO.getId() <= 0)
        {
            throw new BadRequestException("Quiz ID must be a positive number");
        }

        if (!quizRepository.existsById(quizDTO.getId()))
        {
            throw new QuizNotFoundException(quizDTO.getId());
        }
        quizRepository.deleteById(quizDTO.getId());
    }

}