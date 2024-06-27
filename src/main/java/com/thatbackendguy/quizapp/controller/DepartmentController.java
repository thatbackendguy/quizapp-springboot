package com.thatbackendguy.quizapp.controller;

import com.thatbackendguy.quizapp.dto.DepartmentDTO;
import com.thatbackendguy.quizapp.service.DepartmentService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<List<DepartmentDTO>> getDepartment(@RequestBody(required = false) DepartmentDTO departmentDTO, HttpServletRequest request)
    {


        var departments = departmentService.getDepartment(departmentDTO, request);

        return ResponseEntity.ok(departments);
    }

    @PostMapping
    public ResponseEntity<DepartmentDTO> createDepartment(@RequestBody DepartmentDTO departmentDTO, HttpServletRequest request)
    {
        var createdDepartment = departmentService.createDepartment(departmentDTO, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdDepartment);
    }

    @PutMapping
    public ResponseEntity<DepartmentDTO> updateDepartment(@RequestBody DepartmentDTO departmentDTO, HttpServletRequest request)
    {

        var updatedDepartment = departmentService.updateDepartment(departmentDTO, request);

        return ResponseEntity.ok(updatedDepartment);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteDepartment(@RequestBody DepartmentDTO departmentDTO, HttpServletRequest request)
    {

        departmentService.deleteDepartment(departmentDTO, request);

        return ResponseEntity.noContent().build();
    }

}