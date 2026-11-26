package com.fin.tiseo.grading_service.service;

import com.fin.tiseo.grading_service.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository repository;
}