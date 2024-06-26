package com.thatbackendguy.quizapp.controller;

import com.thatbackendguy.quizapp.dto.DepartmentDTO;
import com.thatbackendguy.quizapp.exception.BadRequestException;
import com.thatbackendguy.quizapp.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController
{

    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService)
    {

        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDTO>> getDepartment(@RequestBody(required = false) DepartmentDTO departmentDTO)
    {

        var departments = departmentService.getDepartment(departmentDTO);

        return ResponseEntity.ok(departments);
    }

    @PostMapping
    public ResponseEntity<DepartmentDTO> createDepartment(@RequestBody DepartmentDTO departmentDTO)
    {

        var createdDepartment = departmentService.createDepartment(departmentDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdDepartment);
    }

    @PutMapping
    public ResponseEntity<DepartmentDTO> updateDepartment(@RequestBody DepartmentDTO departmentDTO)
    {

        if (departmentDTO.getId() == null)
        {
            throw new BadRequestException("Department ID is required");
        }
        else if (departmentDTO.getId() <= 0)
        {
            throw new BadRequestException("Department ID must be a positive number");
        }
        else if (departmentDTO.getName().isEmpty())
        {
            throw new BadRequestException("Department name is required");
        }

        var updatedDepartment = departmentService.updateDepartment(departmentDTO.getId(), departmentDTO);

        return ResponseEntity.ok(updatedDepartment);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteDepartment(@RequestBody DepartmentDTO departmentDTO)
    {

        if (departmentDTO.getId() == null)
        {
            throw new BadRequestException("Department ID is required");
        }
        else if (departmentDTO.getId() <= 0)
        {
            throw new BadRequestException("Department ID must be a positive number");
        }

        departmentService.deleteDepartment(departmentDTO.getId());

        return ResponseEntity.noContent().build();
    }

}