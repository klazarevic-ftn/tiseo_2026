package com.fin.tiseo.curriculum_service.repository;

import com.fin.tiseo.curriculum_service.entity.StudyProgram;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyProgramRepository
        extends JpaRepository<StudyProgram, Long> {
}