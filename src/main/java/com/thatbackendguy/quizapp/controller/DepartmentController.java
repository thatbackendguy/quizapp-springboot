package com.thatbackendguy.quizapp.controller;

import com.thatbackendguy.quizapp.dto.DepartmentDTO;
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

        var updatedDepartment = departmentService.updateDepartment(departmentDTO);

        return ResponseEntity.ok(updatedDepartment);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteDepartment(@RequestBody DepartmentDTO departmentDTO)
    {

        departmentService.deleteDepartment(departmentDTO);

        return ResponseEntity.noContent().build();
    }

}