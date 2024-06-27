package com.thatbackendguy.quizapp.controller;

import com.thatbackendguy.quizapp.dto.QuizResponseDTO;
import com.thatbackendguy.quizapp.dto.QuizResultResponseDTO;
import com.thatbackendguy.quizapp.dto.QuizSubmit;
import com.thatbackendguy.quizapp.dto.UserDTO;
import com.thatbackendguy.quizapp.exception.UserNotFoundException;
import com.thatbackendguy.quizapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController
{

    private final UserService userService;

    @Autowired
    public UserController(UserService userService)
    {

        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers(@RequestBody(required = false) UserDTO userDTO, HttpServletRequest request)
    {

        var users = userService.getUsers(userDTO, request);

        return ResponseEntity.ok(users);
    }

    @PutMapping
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO, HttpServletRequest request)
    {

        var updatedUser = userService.updateUser(userDTO, request);

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestBody UserDTO userDTO, HttpServletRequest request)
    {

        userService.deleteUser(userDTO, request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/home")
    public ResponseEntity<List<QuizResponseDTO>> getUserQuizzes(HttpServletRequest request)
    {

        var username = request.getAttribute("username");

        if (username == null) throw new UserNotFoundException();

        return ResponseEntity.ok(userService.getQuizzes(username.toString()));
    }

    @PostMapping("/submit-quiz")
    public ResponseEntity<QuizResultResponseDTO> submitQuiz(@RequestBody List<QuizSubmit> quizSubmitDTO, HttpServletRequest request)
    {

        var username = request.getAttribute("username");

        if (username == null) throw new UserNotFoundException();

        return ResponseEntity.ok(userService.getQuizResult(username.toString(), quizSubmitDTO));
    }

}
