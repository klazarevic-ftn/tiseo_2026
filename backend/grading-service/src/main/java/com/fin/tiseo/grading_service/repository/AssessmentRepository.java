package com.fin.tiseo.grading_service.repository;

import com.fin.tiseo.grading_service.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentRepository
        extends JpaRepository<Assessment, Long> {
}