package com.fin.tiseo.grading_service.service;

import com.fin.tiseo.grading_service.repository.AssessmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final AssessmentRepository repository;
}