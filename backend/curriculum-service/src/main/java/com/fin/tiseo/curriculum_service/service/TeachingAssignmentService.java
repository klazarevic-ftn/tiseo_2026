package com.fin.tiseo.curriculum_service.service;

import com.fin.tiseo.curriculum_service.repository.TeachingAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeachingAssignmentService {

    private final TeachingAssignmentRepository repository;
}