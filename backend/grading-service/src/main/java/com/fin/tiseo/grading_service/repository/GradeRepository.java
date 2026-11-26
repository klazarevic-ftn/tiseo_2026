package com.fin.tiseo.grading_service.repository;

import com.fin.tiseo.grading_service.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> {
}