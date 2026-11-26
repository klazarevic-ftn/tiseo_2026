package com.fin.tiseo.curriculum_service.service;

import com.fin.tiseo.curriculum_service.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository repository;
}