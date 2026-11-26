package com.fin.tiseo.grading_service.service;

import com.fin.tiseo.grading_service.repository.StudentAssessmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentAssessmentService {

    private final StudentAssessmentRepository repository;
}