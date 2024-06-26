package com.thatbackendguy.quizapp.service;

import com.thatbackendguy.quizapp.dto.QuizDTO;
import com.thatbackendguy.quizapp.dto.UserDTO;
import com.thatbackendguy.quizapp.dto.UserUpdateDTO;
import com.thatbackendguy.quizapp.entity.DepartmentEntity;
import com.thatbackendguy.quizapp.entity.UserEntity;
import com.thatbackendguy.quizapp.exception.DepartmentNotFoundException;
import com.thatbackendguy.quizapp.exception.UserNotFoundException;
import com.thatbackendguy.quizapp.repository.DepartmentRepository;
import com.thatbackendguy.quizapp.repository.QuizRepository;
import com.thatbackendguy.quizapp.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public List<UserDTO> getAllUsers()
    {

        var users = userRepository.findAll();

        if (users.isEmpty()) throw new UserNotFoundException();

        return users.stream()
                .map(userEntity -> modelMapper.map(userEntity, UserDTO.class))
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id)
    {

        var userEntity = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        return modelMapper.map(userEntity, UserDTO.class);
    }

    public UserDTO createUser(UserEntity user)
    {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        var userEntity = modelMapper.map(user, UserEntity.class);

        userEntity = userRepository.save(userEntity);

        return modelMapper.map(userEntity, UserDTO.class);
    }

    public UserDTO updateUser(Long id, UserUpdateDTO userUpdateDTO)
    {

        var userEntity = userRepository.findById(id).map(user ->
        {
            user.setName(userUpdateDTO.getName());
            user.setEmail(userUpdateDTO.getEmail());

            if (userUpdateDTO.getDepartmentId() != null)
            {
                DepartmentEntity departmentEntity = departmentRepository.findById(userUpdateDTO.getDepartmentId())
                        .orElseThrow(() -> new DepartmentNotFoundException(userUpdateDTO.getDepartmentId()));
                user.setDepartment(departmentEntity);
            }

            return userRepository.save(user);
        }).orElseThrow(() -> new UserNotFoundException(id));

        return modelMapper.map(userEntity, UserDTO.class);
    }

    public void deleteUser(Long id)
    {

        if (!userRepository.existsById(id))
        {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    public List<QuizDTO> getQuizzes(String username)
    {

        var userEntity = userRepository.findByUsername(username);

        if (userEntity == null) throw new UserNotFoundException(username);

        var quizzes = quizRepository.findByDeptId(userEntity.getDepartment().getId());

        return quizzes.stream()
            .map(quizEntity -> modelMapper.map(quizEntity, QuizDTO.class))
            .collect(Collectors.toList());
    }

}