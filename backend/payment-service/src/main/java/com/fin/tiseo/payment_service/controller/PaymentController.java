package com.fin.tiseo.payment_service.controller;

import com.fin.tiseo.payment_service.entity.Payment;
import com.fin.tiseo.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @GetMapping
    public List<Payment> findAll(@RequestParam(required = false) Long studentId) {
        return service.findAll(studentId);
    }

    @GetMapping("/{id}")
    public Payment findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<Payment> create(@Valid @RequestBody Payment request) {
        Payment created = service.create(request);
        return ResponseEntity.created(URI.create("/api/payments/" + created.getId()))
                .body(created);
    }
}
