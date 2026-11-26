package com.fin.tiseo.grading_service.repository;

import com.fin.tiseo.grading_service.entity.StudentAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentAssessmentRepository
        extends JpaRepository<StudentAssessment, Long> {
}