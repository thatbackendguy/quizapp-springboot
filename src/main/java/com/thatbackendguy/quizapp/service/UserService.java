package com.thatbackendguy.quizapp.service;

import com.thatbackendguy.quizapp.dto.QuizResponseDTO;
import com.thatbackendguy.quizapp.dto.UserDTO;
import com.thatbackendguy.quizapp.entity.DepartmentEntity;
import com.thatbackendguy.quizapp.entity.UserEntity;
import com.thatbackendguy.quizapp.exception.BadRequestException;
import com.thatbackendguy.quizapp.exception.DepartmentNotFoundException;
import com.thatbackendguy.quizapp.exception.UserNotFoundException;
import com.thatbackendguy.quizapp.repository.DepartmentRepository;
import com.thatbackendguy.quizapp.repository.QuizRepository;
import com.thatbackendguy.quizapp.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService
{

    private final UserRepository userRepository;

    private final DepartmentRepository departmentRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    private final QuizRepository quizRepository;

    @Autowired
    public UserService(UserRepository userRepository, DepartmentRepository departmentRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, QuizRepository quizRepository)
    {

        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.modelMapper = modelMapper;
        this.quizRepository = quizRepository;
    }

    public List<UserDTO> getUsers(UserDTO userDTO)
    {

        List<UserEntity> users;

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

        if (users.isEmpty())
        {
            throw new UserNotFoundException();
        }

        return users.stream()
                .map(userEntity -> modelMapper.map(userEntity, UserDTO.class))
                .collect(Collectors.toList());

    }

    public UserDTO createUser(UserEntity user)
    {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        var deptId = user.getDepartment().getId();

        var dept = departmentRepository.findById(deptId).orElseThrow(() -> new DepartmentNotFoundException(deptId));

        user.setDepartment(dept);

        var userEntity = modelMapper.map(user, UserEntity.class);

        userEntity = userRepository.save(userEntity);

        return modelMapper.map(userEntity, UserDTO.class);
    }

    public UserDTO updateUser(UserDTO userDTO)
    {

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

            if (userDTO.getDepartmentId() != null)
            {
                DepartmentEntity departmentEntity = departmentRepository.findById(userDTO.getDepartmentId())
                        .orElseThrow(() -> new DepartmentNotFoundException(userDTO.getDepartmentId()));
                user.setDepartment(departmentEntity);
            }

            return userRepository.save(user);
        }).orElseThrow(() -> new UserNotFoundException(userDTO.getId()));

        return modelMapper.map(userEntity, UserDTO.class);
    }

    public void deleteUser(UserDTO userDTO)
    {
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

}