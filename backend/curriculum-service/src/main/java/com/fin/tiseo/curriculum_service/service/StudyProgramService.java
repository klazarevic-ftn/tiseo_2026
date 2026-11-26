package com.fin.tiseo.curriculum_service.service;

import com.fin.tiseo.curriculum_service.repository.StudyProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyProgramService {

    private final StudyProgramRepository repository;
}