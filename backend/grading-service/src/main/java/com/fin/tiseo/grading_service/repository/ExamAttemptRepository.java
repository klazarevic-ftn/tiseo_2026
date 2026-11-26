package com.fin.tiseo.grading_service.repository;

import com.fin.tiseo.grading_service.entity.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamAttemptRepository
        extends JpaRepository<ExamAttempt, Long> {
}