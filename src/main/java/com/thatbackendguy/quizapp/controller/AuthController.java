package com.thatbackendguy.quizapp.controller;

import com.thatbackendguy.quizapp.dto.JwtRequestDTO;
import com.thatbackendguy.quizapp.dto.JwtResponseDTO;
import com.thatbackendguy.quizapp.dto.UserDTO;
import com.thatbackendguy.quizapp.entity.UserEntity;
import com.thatbackendguy.quizapp.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController
{

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService)
    {

        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@RequestBody JwtRequestDTO jwtRequestDTO)
    {

        return ResponseEntity.ok(authService.login(jwtRequestDTO));
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserEntity userEntity)
    {

        var createdUser = authService.signup(userEntity);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public String exceptionHandler()
    {

        return "Credentials Invalid !!";
    }

}
