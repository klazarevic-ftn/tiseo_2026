package com.fin.tiseo.grading_service.service;

import com.fin.tiseo.grading_service.repository.ExamAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExamAttemptService {

    private final ExamAttemptRepository repository;
}