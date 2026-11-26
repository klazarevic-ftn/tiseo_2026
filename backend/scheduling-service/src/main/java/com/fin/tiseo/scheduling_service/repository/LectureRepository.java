package com.fin.tiseo.scheduling_service.repository;

import com.fin.tiseo.scheduling_service.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
}