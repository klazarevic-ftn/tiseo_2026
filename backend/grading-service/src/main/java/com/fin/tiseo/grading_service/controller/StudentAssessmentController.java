package com.fin.tiseo.grading_service.controller;

import com.fin.tiseo.grading_service.service.StudentAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student-assessments")
@RequiredArgsConstructor
public class StudentAssessmentController {

    private final StudentAssessmentService service;
}