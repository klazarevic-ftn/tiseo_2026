package com.fin.tiseo.payment_service.controller;

import com.fin.tiseo.payment_service.entity.StudentAccount;
import com.fin.tiseo.payment_service.service.StudentAccountService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student-accounts")
@RequiredArgsConstructor
public class StudentAccountController {

    private final StudentAccountService service;

    @GetMapping
    public List<StudentAccount> findAll(@RequestParam(required = false) Long studentId) {
        return service.findAll(studentId);
    }

    @GetMapping("/{id}")
    public StudentAccount findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<StudentAccount> create(@Valid @RequestBody StudentAccount request) {
        StudentAccount created = service.create(request);
        return ResponseEntity.created(URI.create("/api/student-accounts/" + created.getId()))
                .body(created);
    }
}
