package com.fin.tiseo.user_service.controller;

import com.fin.tiseo.user_service.entity.Administrator;
import com.fin.tiseo.user_service.service.AdministratorService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/administrators")
@RequiredArgsConstructor
public class AdministratorController {

    private final AdministratorService service;

    @GetMapping
    public List<Administrator> findAll(
            @RequestParam(required = false) Long userId
    ) {
        return service.findAll(userId);
    }

    @GetMapping("/{id}")
    public Administrator findById(@PathVariable Long id) {
        return service.findById(id);
    }
}