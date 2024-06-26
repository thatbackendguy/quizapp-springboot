package com.thatbackendguy.quizapp.repository;

import com.thatbackendguy.quizapp.entity.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizRepository extends JpaRepository<QuizEntity, Long>
{

    @Query("select q from QuizEntity q where q.department.id=:deptId")
    List<QuizEntity> findByDeptId(@Param("deptId") Long deptId);

}
