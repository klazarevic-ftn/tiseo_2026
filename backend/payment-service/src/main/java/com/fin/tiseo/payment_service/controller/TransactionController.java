package com.fin.tiseo.payment_service.controller;

import com.fin.tiseo.payment_service.entity.Transaction;
import com.fin.tiseo.payment_service.service.TransactionService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;

    @GetMapping
    public List<Transaction> findAll(@RequestParam(required = false) Long studentAccountId) {
        return service.findAll(studentAccountId);
    }

    @GetMapping("/{id}")
    public Transaction findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<Transaction> create(@Valid @RequestBody Transaction request) {
        Transaction created = service.create(request);
        return ResponseEntity.created(URI.create("/api/transactions/" + created.getId()))
                .body(created);
    }
}
