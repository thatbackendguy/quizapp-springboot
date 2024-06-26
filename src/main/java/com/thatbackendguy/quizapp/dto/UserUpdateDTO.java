package com.thatbackendguy.quizapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO
{

    private String name;

    private String email;

    private Long departmentId;

}
