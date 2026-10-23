package com.fin.tiseo.user_service.controller;

import com.fin.tiseo.user_service.entity.Student;
import com.fin.tiseo.user_service.service.StudentService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService service;

    @GetMapping
    public List<Student> findAll(@RequestParam(required = false) Long userId) {
        return service.findAll(userId);
    }

    @GetMapping("/{id}")
    public Student findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<Student> create(@Valid @RequestBody Student request) {
        Student created = service.create(request);
        return ResponseEntity.created(URI.create("/api/students/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @Valid @RequestBody Student request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
