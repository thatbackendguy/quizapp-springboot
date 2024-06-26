package com.thatbackendguy.quizapp.repository;

import com.thatbackendguy.quizapp.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long>
{

    @Query("SELECT d FROM DepartmentEntity d WHERE d.name = :name")
    DepartmentEntity findByName(@Param("name") String name);

}
