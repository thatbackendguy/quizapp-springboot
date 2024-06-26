package com.thatbackendguy.quizapp.service;

import com.thatbackendguy.quizapp.dto.DepartmentDTO;
import com.thatbackendguy.quizapp.entity.DepartmentEntity;
import com.thatbackendguy.quizapp.exception.BadRequestException;
import com.thatbackendguy.quizapp.exception.DepartmentNotFoundException;
import com.thatbackendguy.quizapp.repository.DepartmentRepository;
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

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository, ModelMapper modelMapper)
    {

        this.departmentRepository = departmentRepository;
        this.modelMapper = modelMapper;
    }

    public List<DepartmentDTO> getDepartment(DepartmentDTO departmentDTO)
    {

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

    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO)
    {

        if (departmentDTO.getName().isEmpty()) throw new BadRequestException("Name is required");

        var departmentEntity = modelMapper.map(departmentDTO, DepartmentEntity.class);

        departmentEntity = departmentRepository.save(departmentEntity);

        return modelMapper.map(departmentEntity, DepartmentDTO.class);
    }

    public DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDetails)
    {

        if (id == null)
        {
            throw new BadRequestException("ID is required");
        }

        var departmentEntity = departmentRepository.findById(id).map(department ->
        {
            department.setName(departmentDetails.getName());
            return departmentRepository.save(department);
        }).orElseThrow(() -> new DepartmentNotFoundException(id));

        return modelMapper.map(departmentEntity, DepartmentDTO.class);
    }

    public void deleteDepartment(Long id)
    {

        if (id == null)
        {
            throw new BadRequestException("ID is required");
        }

        if (!departmentRepository.existsById(id))
        {
            throw new DepartmentNotFoundException(id);
        }
        departmentRepository.deleteById(id);
    }

}