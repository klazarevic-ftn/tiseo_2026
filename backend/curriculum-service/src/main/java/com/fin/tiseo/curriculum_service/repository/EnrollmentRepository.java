package com.fin.tiseo.curriculum_service.repository;

import com.fin.tiseo.curriculum_service.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository
        extends JpaRepository<Enrollment, Long> {
}