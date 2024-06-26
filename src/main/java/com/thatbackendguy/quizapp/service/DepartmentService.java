package com.thatbackendguy.quizapp.service;

import com.thatbackendguy.quizapp.dto.DepartmentDTO;
import com.thatbackendguy.quizapp.entity.DepartmentEntity;
import com.thatbackendguy.quizapp.exception.BadRequestException;
import com.thatbackendguy.quizapp.exception.DepartmentNotFoundException;
import com.thatbackendguy.quizapp.repository.DepartmentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        if (departmentDTO.getId() == null) // id field is not available in body
        {
            var departments = departmentRepository.findAll();

            if (departments.isEmpty()) throw new DepartmentNotFoundException();

            return departments.stream()
                    .map(dept -> modelMapper.map(dept, DepartmentDTO.class))
                    .collect(Collectors.toList());
        }

        if (departmentDTO.getId() <= 0) throw new BadRequestException();

        var departments = departmentRepository.findById(departmentDTO.getId());

        if (departments.isEmpty()) throw new DepartmentNotFoundException();

        return departments.stream()
                .map(dept -> modelMapper.map(dept, DepartmentDTO.class))
                .collect(Collectors.toList());
    }

    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO)
    {

        var departmentEntity = modelMapper.map(departmentDTO, DepartmentEntity.class);

        departmentEntity = departmentRepository.save(departmentEntity);

        return modelMapper.map(departmentEntity, DepartmentDTO.class);
    }

    public DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDetails)
    {

        if (id == null) throw new BadRequestException();

        var departmentEntity = departmentRepository.findById(id).map(department ->
        {
            department.setName(departmentDetails.getName());
            return departmentRepository.save(department);
        }).orElseThrow(() -> new DepartmentNotFoundException(id));

        return modelMapper.map(departmentEntity, DepartmentDTO.class);
    }

    public void deleteDepartment(Long id)
    {

        if (id == null) throw new BadRequestException();

        if (!departmentRepository.existsById(id))
        {
            throw new DepartmentNotFoundException(id);
        }
        departmentRepository.deleteById(id);
    }

}