package com.thatbackendguy.quizapp.service;

import com.thatbackendguy.quizapp.dto.DepartmentDTO;
import com.thatbackendguy.quizapp.entity.DepartmentEntity;
import com.thatbackendguy.quizapp.exception.BadRequestException;
import com.thatbackendguy.quizapp.exception.DepartmentNotFoundException;
import com.thatbackendguy.quizapp.repository.DepartmentRepository;
import com.thatbackendguy.quizapp.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService
{

    private final DepartmentRepository departmentRepository;

    private final ModelMapper modelMapper;

    private final UserRepository userRepository;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository, ModelMapper modelMapper, UserRepository userRepository)
    {

        this.departmentRepository = departmentRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    public List<DepartmentDTO> getDepartment(DepartmentDTO departmentDTO, HttpServletRequest request)
    {

        var currentUser = request.getAttribute("username");

        if (currentUser == null) throw new RuntimeException("Unauthorized user");

        if (!userRepository.findByUsername(currentUser.toString()).getDepartment().getName().equals("Admin"))
            throw new RuntimeException("Unauthorized user");

        List<DepartmentEntity> departments;

        if (departmentDTO == null || departmentDTO.getId() == null)
        {
            departments = departmentRepository.findAll();
        }
        else if (departmentDTO.getId() > 0)
        {
            departments = departmentRepository.findById(departmentDTO.getId())
                    .map(Collections::singletonList)
                    .orElseThrow(() -> new DepartmentNotFoundException(departmentDTO.getId()));
        }
        else
        {
            throw new BadRequestException("Department ID must be a positive number");
        }

        if (departments.isEmpty())
        {
            throw new DepartmentNotFoundException();
        }

        return departments.stream()
                .map(dept -> modelMapper.map(dept, DepartmentDTO.class))
                .collect(Collectors.toList());
    }

    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO, HttpServletRequest request)
    {

        var currentUser = request.getAttribute("username");

        if (currentUser == null) throw new RuntimeException("Unauthorized user");

        if (!userRepository.findByUsername(currentUser.toString()).getDepartment().getName().equals("Admin"))
            throw new RuntimeException("Unauthorized user");

        if (departmentDTO.getName().isEmpty()) throw new BadRequestException("Name is required");

        var departmentEntity = modelMapper.map(departmentDTO, DepartmentEntity.class);

        departmentEntity = departmentRepository.save(departmentEntity);

        return modelMapper.map(departmentEntity, DepartmentDTO.class);
    }

    public DepartmentDTO updateDepartment(DepartmentDTO departmentDTO, HttpServletRequest request)
    {

        var currentUser = request.getAttribute("username");

        if (currentUser == null) throw new RuntimeException("Unauthorized user");

        if (!userRepository.findByUsername(currentUser.toString()).getDepartment().getName().equals("Admin"))
            throw new RuntimeException("Unauthorized user");

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

        var departmentEntity = departmentRepository.findById(departmentDTO.getId()).map(department ->
        {
            department.setName(departmentDTO.getName());
            return departmentRepository.save(department);
        }).orElseThrow(() -> new DepartmentNotFoundException(departmentDTO.getId()));

        return modelMapper.map(departmentEntity, DepartmentDTO.class);
    }

    public void deleteDepartment(DepartmentDTO departmentDTO, HttpServletRequest request)
    {

        var currentUser = request.getAttribute("username");

        if (currentUser == null) throw new RuntimeException("Unauthorized user");

        if (!userRepository.findByUsername(currentUser.toString()).getDepartment().getName().equals("Admin"))
            throw new RuntimeException("Unauthorized user");

        if (departmentDTO.getId() == null)
        {
            throw new BadRequestException("Department ID is required");
        }
        else if (departmentDTO.getId() <= 0)
        {
            throw new BadRequestException("Department ID must be a positive number");
        }

        if (!departmentRepository.existsById(departmentDTO.getId()))
        {
            throw new DepartmentNotFoundException(departmentDTO.getId());
        }
        departmentRepository.deleteById(departmentDTO.getId());
    }

}