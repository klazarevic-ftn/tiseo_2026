package com.fin.tiseo.grading_service.controller;

import com.fin.tiseo.grading_service.service.ExamAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exam-attempts")
@RequiredArgsConstructor
public class ExamAttemptController {

    private final ExamAttemptService service;
}