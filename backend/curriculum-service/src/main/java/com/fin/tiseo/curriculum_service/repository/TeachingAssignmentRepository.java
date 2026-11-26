package com.fin.tiseo.curriculum_service.repository;

import com.fin.tiseo.curriculum_service.entity.TeachingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeachingAssignmentRepository
        extends JpaRepository<TeachingAssignment, Long> {
}