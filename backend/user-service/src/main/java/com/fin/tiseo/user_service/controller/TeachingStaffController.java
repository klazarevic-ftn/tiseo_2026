package com.fin.tiseo.user_service.controller;

import com.fin.tiseo.user_service.entity.TeachingStaff;
import com.fin.tiseo.user_service.service.TeachingStaffService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teaching-staff")
@RequiredArgsConstructor
public class TeachingStaffController {

    private final TeachingStaffService service;

    @GetMapping
    public List<TeachingStaff> findAll(
            @RequestParam(required = false) Long userId
    ) {
        return service.findAll(userId);
    }

    @GetMapping("/{id}")
    public TeachingStaff findById(@PathVariable Long id) {
        return service.findById(id);
    }
}