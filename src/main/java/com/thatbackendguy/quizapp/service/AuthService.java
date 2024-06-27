package com.thatbackendguy.quizapp.service;

import com.thatbackendguy.quizapp.dto.JwtRequestDTO;
import com.thatbackendguy.quizapp.dto.JwtResponseDTO;
import com.thatbackendguy.quizapp.dto.UserDTO;
import com.thatbackendguy.quizapp.entity.UserEntity;
import com.thatbackendguy.quizapp.exception.BadRequestException;
import com.thatbackendguy.quizapp.exception.DepartmentNotFoundException;
import com.thatbackendguy.quizapp.exception.InvalidCredentialsException;
import com.thatbackendguy.quizapp.repository.DepartmentRepository;
import com.thatbackendguy.quizapp.repository.UserRepository;
import com.thatbackendguy.quizapp.utils.JwtUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService
{

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final DepartmentRepository departmentRepository;

    private final JwtUtils jwtUtils;

    @Autowired
    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository, ModelMapper modelMapper, DepartmentRepository departmentRepository, JwtUtils jwtUtils)
    {

        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.departmentRepository = departmentRepository;
        this.jwtUtils = jwtUtils;
    }

    public UserDTO signup(UserEntity newUserEntity)
    {

        if (newUserEntity.getUsername().isEmpty())
        {
            throw new BadRequestException("Username is required");
        }
        else if (newUserEntity.getPassword().isEmpty())
        {
            throw new BadRequestException("Password is required");
        }
        else if (newUserEntity.getName().isEmpty())
        {
            throw new BadRequestException("Name is required");
        }
        else if (newUserEntity.getEmail().isEmpty())
        {
            throw new BadRequestException("Email is required");
        }
        else if (newUserEntity.getDepartment().getId() <= 0)
        {
            throw new BadRequestException("Valid department ID is required");
        }

        newUserEntity.setPassword(passwordEncoder.encode(newUserEntity.getPassword()));

        var deptId = newUserEntity.getDepartment().getId();

        var dept = departmentRepository.findById(deptId).orElseThrow(() -> new DepartmentNotFoundException(deptId));

        newUserEntity.setDepartment(dept);

        var userEntity = modelMapper.map(newUserEntity, UserEntity.class);

        userEntity = userRepository.save(userEntity);

        return modelMapper.map(userEntity, UserDTO.class);
    }

    public JwtResponseDTO login(JwtRequestDTO request)
    {

        if (request.getUsername().isEmpty())
        {
            throw new BadRequestException("Username is required");
        }
        else if (request.getPassword().isEmpty())
        {
            throw new BadRequestException("Password is required");
        }

        var userEntity = userRepository.findByUsername(request.getUsername());

        if (!passwordEncoder.matches(request.getPassword(), userEntity.getPassword()))
        {
            throw new InvalidCredentialsException();
        }

        var token = jwtUtils.generateToken(userEntity.getUsername());

        return JwtResponseDTO.builder().jwtToken(token).username(userEntity.getUsername()).build();
    }

}
