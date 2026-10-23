package com.fin.tiseo.user_service.controller;

import com.fin.tiseo.user_service.entity.TeachingStaff;
import com.fin.tiseo.user_service.service.TeachingStaffService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teaching-staff")
@RequiredArgsConstructor
public class TeachingStaffController {

    private final TeachingStaffService service;

    @GetMapping
    public List<TeachingStaff> findAll(@RequestParam(required = false) Long userId) {
        return service.findAll(userId);
    }

    @GetMapping("/{id}")
    public TeachingStaff findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<TeachingStaff> create(@Valid @RequestBody TeachingStaff request) {
        TeachingStaff created = service.create(request);
        return ResponseEntity.created(URI.create("/api/teaching-staff/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    public TeachingStaff update(@PathVariable Long id, @Valid @RequestBody TeachingStaff request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
