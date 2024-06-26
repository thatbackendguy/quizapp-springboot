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

        if (quizDTO.getId() != null)
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
            if (quizzes.isEmpty())
            {
                throw new QuizNotFoundException(quizDTO.getDeptName());
            }
        }
        else
        {
            quizzes = quizRepository.findAll();
            if (quizzes.isEmpty())
            {
                throw new QuizNotFoundException();
            }
        }

        return quizzes.stream()
                .map(quizEntity -> modelMapper.map(quizEntity, QuizResponseDTO.class))
                .collect(Collectors.toList());
    }

    public QuizDTO createQuiz(QuizDTO quizDTO)
    {

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

    public QuizDTO updateQuiz(Long id, QuizDTO quizDetails)
    {

        if (id == null) throw new BadRequestException();

        var quizEntity = quizRepository.findById(id).map(quiz ->
        {
            quiz.setQuestion(quizDetails.getQuestion());
            quiz.setOptions(quizDetails.getOptions());
            quiz.setAnswer(quizDetails.getAnswer());
            if (quizDetails.getDepartment() != null && quizDetails.getDepartment().getId() != null)
            {
                var department = departmentRepository.findById(quizDetails.getDepartment().getId())
                        .orElseThrow(() -> new DepartmentNotFoundException(quizDetails.getDepartment().getId()));

                quiz.setDepartment(department);
            }
            return quizRepository.save(quiz);
        }).orElseThrow(() -> new QuizNotFoundException(id));

        return modelMapper.map(quizEntity, QuizDTO.class);
    }

    public void deleteQuiz(Long id)
    {

        if (id == null) throw new BadRequestException();

        if (!quizRepository.existsById(id))
        {
            throw new QuizNotFoundException(id);
        }
        quizRepository.deleteById(id);
    }

}