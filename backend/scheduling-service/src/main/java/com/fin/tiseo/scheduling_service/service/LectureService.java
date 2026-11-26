package com.fin.tiseo.scheduling_service.service;

import com.fin.tiseo.scheduling_service.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureRepository repository;
}