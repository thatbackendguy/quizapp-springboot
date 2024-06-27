package com.thatbackendguy.quizapp.service;

import com.thatbackendguy.quizapp.dto.QuizResponseDTO;
import com.thatbackendguy.quizapp.dto.QuizResultResponseDTO;
import com.thatbackendguy.quizapp.dto.QuizSubmit;
import com.thatbackendguy.quizapp.dto.UserDTO;
import com.thatbackendguy.quizapp.entity.DepartmentEntity;
import com.thatbackendguy.quizapp.entity.UserEntity;
import com.thatbackendguy.quizapp.exception.BadRequestException;
import com.thatbackendguy.quizapp.exception.DepartmentNotFoundException;
import com.thatbackendguy.quizapp.exception.QuizNotFoundException;
import com.thatbackendguy.quizapp.exception.UserNotFoundException;
import com.thatbackendguy.quizapp.repository.DepartmentRepository;
import com.thatbackendguy.quizapp.repository.QuizRepository;
import com.thatbackendguy.quizapp.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService
{

    private final UserRepository userRepository;

    private final DepartmentRepository departmentRepository;

    private final ModelMapper modelMapper;

    private final QuizRepository quizRepository;

    @Autowired
    public UserService(UserRepository userRepository, DepartmentRepository departmentRepository, ModelMapper modelMapper, QuizRepository quizRepository)
    {

        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.modelMapper = modelMapper;
        this.quizRepository = quizRepository;
    }

    public List<UserDTO> getUsers(UserDTO userDTO, HttpServletRequest request)
    {

        var currentUsername = request.getAttribute("username");

        if (currentUsername == null) throw new RuntimeException("Unauthorized user");

        var currentUserEntity = userRepository.findByUsername(currentUsername.toString());

        if (currentUserEntity == null) throw new RuntimeException("User not found");

        List<UserEntity> users;

        if (currentUserEntity.getDepartment().getName().equals("Admin"))
        {
            if (userDTO == null || userDTO.getId() == null)
            {
                users = userRepository.findAll();
            }
            else if (userDTO.getId() > 0)
            {
                users = userRepository.findById(userDTO.getId())
                        .map(Collections::singletonList)
                        .orElseThrow(() -> new UserNotFoundException(userDTO.getId()));
            }
            else
            {
                users = userRepository.findAll();
            }
        }
        else
        {
            users = Collections.singletonList(currentUserEntity);
        }

        if (users.isEmpty())
        {
            throw new UserNotFoundException();
        }

        return users.stream()
                .map(userEntity -> modelMapper.map(userEntity, UserDTO.class))
                .collect(Collectors.toList());
    }

    public UserDTO updateUser(UserDTO userDTO, HttpServletRequest request)
    {

        var currentUsername = request.getAttribute("username");

        if (currentUsername == null) throw new RuntimeException("Unauthorized user");

        var currentUserEntity = userRepository.findByUsername(currentUsername.toString());

        if (currentUserEntity == null) throw new RuntimeException("User not found");

        boolean isAdmin = currentUserEntity.getDepartment().getName().equals("Admin");

        if (!isAdmin && !currentUserEntity.getId().equals(userDTO.getId()))
        {
            throw new RuntimeException("Unauthorized: You can only update your own profile");
        }

        if (userDTO.getId() == null)
        {
            throw new BadRequestException("User ID is required");
        }
        else if (userDTO.getId() <= 0)
        {
            throw new BadRequestException("User ID must be a positive number");
        }
        else if (userDTO.getDepartmentId() == null)
        {
            throw new BadRequestException("Department ID is required");
        }
        else if (userDTO.getDepartmentId() <= 0)
        {
            throw new BadRequestException("Department ID must be a positive number");
        }
        else if (userDTO.getEmail().isEmpty())
        {
            throw new BadRequestException("Email is required");
        }
        else if (userDTO.getName().isEmpty())
        {
            throw new BadRequestException("Name is required");
        }

        var userEntity = userRepository.findById(userDTO.getId()).map(user ->
        {
            user.setName(userDTO.getName());
            
            user.setEmail(userDTO.getEmail());

            if (isAdmin && userDTO.getDepartmentId() != null)
            {
                DepartmentEntity departmentEntity = departmentRepository.findById(userDTO.getDepartmentId())
                        .orElseThrow(() -> new DepartmentNotFoundException(userDTO.getDepartmentId()));
                user.setDepartment(departmentEntity);
            }

            return userRepository.save(user);
        }).orElseThrow(() -> new UserNotFoundException(userDTO.getId()));

        return modelMapper.map(userEntity, UserDTO.class);
    }

    public void deleteUser(UserDTO userDTO, HttpServletRequest request)
    {

        var currentUsername = request.getAttribute("username");

        if (currentUsername == null) throw new RuntimeException("Unauthorized user");

        var currentUserEntity = userRepository.findByUsername(currentUsername.toString());

        if (currentUserEntity == null) throw new RuntimeException("User not found");

        boolean isAdmin = currentUserEntity.getDepartment().getName().equals("Admin");

        if (!isAdmin && !currentUserEntity.getId().equals(userDTO.getId()))
        {
            throw new RuntimeException("Unauthorized: You can only delete your own profile");
        }

        if (userDTO.getId() == null)
        {
            throw new BadRequestException("User ID is required");
        }
        else if (userDTO.getId() <= 0)
        {
            throw new BadRequestException("User ID must be a positive number");
        }
        if (!userRepository.existsById(userDTO.getId()))
        {
            throw new UserNotFoundException(userDTO.getId());
        }
        userRepository.deleteById(userDTO.getId());
    }

    public List<QuizResponseDTO> getQuizzes(String username)
    {

        var userEntity = userRepository.findByUsername(username);

        if (userEntity == null) throw new UserNotFoundException(username);

        var quizzes = quizRepository.findByDeptId(userEntity.getDepartment().getId());

        return quizzes.stream()
                .map(quizEntity -> modelMapper.map(quizEntity, QuizResponseDTO.class))
                .collect(Collectors.toList());
    }

    public QuizResultResponseDTO getQuizResult(String username, List<QuizSubmit> quizSubmitDTO)
    {

        var userEntity = userRepository.findByUsername(username);

        if (userEntity == null) throw new UserNotFoundException(username);

        var quizzes = quizRepository.findByDeptId(userEntity.getDepartment().getId());

        if (quizzes.isEmpty()) throw new QuizNotFoundException(userEntity.getDepartment().getName());

        var totalQuizzes = quizzes.size();

        var correct = 0;

        var results = new ArrayList<QuizSubmit>();

        if (totalQuizzes != quizSubmitDTO.size()) throw new BadRequestException("You must answer all the questions");

        for (int i = 0; i < totalQuizzes; i++)
        {
            var response = quizSubmitDTO.get(i);

            var quiz = quizzes.get(i);

            if (!Objects.equals(quiz.getId(), response.getId())) throw new BadRequestException("Invalid quiz!");

            if (response.getAnswer().equals(quiz.getAnswer()))
            {
                correct++;
                results.add(new QuizSubmit(response.getId(), "true"));
            }
            else
            {
                results.add(new QuizSubmit(response.getId(), "false"));
            }
        }

        var scorePercentage = (float) correct / totalQuizzes * 100;

        return new QuizResultResponseDTO(scorePercentage, results);
    }

}