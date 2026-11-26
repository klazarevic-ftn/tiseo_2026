package com.fin.tiseo.scheduling_service.repository;

import com.fin.tiseo.scheduling_service.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepository extends JpaRepository<Exam, Long> {
}