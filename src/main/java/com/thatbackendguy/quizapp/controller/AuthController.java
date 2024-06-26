package com.thatbackendguy.quizapp.controller;

import com.thatbackendguy.quizapp.dto.JwtRequestDTO;
import com.thatbackendguy.quizapp.dto.JwtResponseDTO;
import com.thatbackendguy.quizapp.dto.UserDTO;
import com.thatbackendguy.quizapp.entity.UserEntity;
import com.thatbackendguy.quizapp.security.JwtHelper;
import com.thatbackendguy.quizapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController
{

    private final UserDetailsService userDetailsService;

    private final UserService userService;

    private final AuthenticationManager manager;

    private final JwtHelper helper;

    @Autowired
    public AuthController(UserService userService, UserDetailsService userDetailsService, AuthenticationManager manager, JwtHelper helper)
    {

        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.manager = manager;
        this.helper = helper;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@RequestBody JwtRequestDTO request)
    {

        this.doAuthenticate(request.getUsername(), request.getPassword());

        var userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        var token = this.helper.generateToken(userDetails);

        var response = JwtResponseDTO.builder().jwtToken(token).username(userDetails.getUsername()).build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-user")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserEntity userEntity)
    {

        var createdUser = userService.createUser(userEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    private void doAuthenticate(String email, String password)
    {

        var authentication = new UsernamePasswordAuthenticationToken(email, password);

        try
        {
            manager.authenticate(authentication);
        }
        catch (BadCredentialsException e)
        {
            throw new BadCredentialsException("Invalid Username or Password  !!");
        }

    }

    @ExceptionHandler(BadCredentialsException.class)
    public String exceptionHandler()
    {

        return "Credentials Invalid !!";
    }

}
