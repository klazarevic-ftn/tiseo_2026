package com.fin.tiseo.file_service.controller;

import com.fin.tiseo.file_service.entity.Document;
import com.fin.tiseo.file_service.service.DocumentService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService service;

    @GetMapping
    public List<Document> findAll(@RequestParam(required = false) Long userId) {
        return service.findAll(userId);
    }

    @GetMapping("/{id}")
    public Document findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<Document> create(@Valid @RequestBody Document request) {
        Document created = service.create(request);
        return ResponseEntity.created(URI.create("/api/documents/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    public Document update(@PathVariable Long id, @Valid @RequestBody Document request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
