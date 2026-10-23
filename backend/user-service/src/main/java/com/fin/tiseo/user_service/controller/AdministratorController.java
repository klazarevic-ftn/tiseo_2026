package com.fin.tiseo.user_service.controller;

import com.fin.tiseo.user_service.entity.Administrator;
import com.fin.tiseo.user_service.service.AdministratorService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/administrators")
@RequiredArgsConstructor
public class AdministratorController {

    private final AdministratorService service;

    @GetMapping
    public List<Administrator> findAll(@RequestParam(required = false) Long userId) {
        return service.findAll(userId);
    }

    @GetMapping("/{id}")
    public Administrator findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<Administrator> create(@Valid @RequestBody Administrator request) {
        Administrator created = service.create(request);
        return ResponseEntity.created(URI.create("/api/administrators/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    public Administrator update(@PathVariable Long id, @Valid @RequestBody Administrator request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
