package com.fin.tiseo.user_service.controller;

import com.fin.tiseo.user_service.entity.Student;
import com.fin.tiseo.user_service.service.StudentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService service;

    @GetMapping
    public List<Student> findAll(
            @RequestParam(required = false) Long userId
    ) {
        return service.findAll(userId);
    }

    @GetMapping("/search")
    public List<Student> search(@RequestParam String search) {
        return service.search(search, null);
    }

    @GetMapping("/{id}")
    public Student findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping("/")
    public List<Student> findByIds(@RequestBody List<Long> ids) {
        return service.findByIds(ids);
    }
}