package com.thatbackendguy.quizapp.controller;

import com.thatbackendguy.quizapp.dto.QuizResponseDTO;
import com.thatbackendguy.quizapp.dto.UserDTO;
import com.thatbackendguy.quizapp.exception.BadRequestException;
import com.thatbackendguy.quizapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<List<UserDTO>> getUsers(@RequestBody(required = false) UserDTO userDTO)
    {

        var users = userService.getUsers(userDTO);

        return ResponseEntity.ok(users);
    }

    @PutMapping
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO)
    {

        if (userDTO.getId() == null || userDTO.getId() <= 0 || userDTO.getDepartmentId() == null || userDTO.getDepartmentId() <= 0 || userDTO.getEmail()
                .isEmpty() || userDTO.getName().isEmpty()) throw new BadRequestException();

        var updatedUser = userService.updateUser(userDTO.getId(), userDTO);

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestBody UserDTO userDTO)
    {

        if (userDTO.getId() == null || userDTO.getId() <= 0) throw new BadRequestException();

        userService.deleteUser(userDTO.getId());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/home")
    public ResponseEntity<List<QuizResponseDTO>> getUserQuizzes()
    {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username;

        if (principal instanceof UserDetails)
        {
            username = ( (UserDetails) principal ).getUsername();
        }
        else
        {
            username = principal.toString();
        }

        return ResponseEntity.ok(userService.getQuizzes(username));
    }

}
