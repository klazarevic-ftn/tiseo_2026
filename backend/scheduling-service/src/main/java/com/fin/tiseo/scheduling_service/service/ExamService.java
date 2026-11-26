package com.fin.tiseo.scheduling_service.service;

import com.fin.tiseo.scheduling_service.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository repository;
}