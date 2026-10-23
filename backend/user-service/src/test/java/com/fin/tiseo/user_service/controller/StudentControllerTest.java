package com.fin.tiseo.user_service.controller;

import com.fin.tiseo.user_service.entity.Student;
import com.fin.tiseo.user_service.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StudentControllerTest {
    private StudentService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(StudentService.class);
        mvc = MockMvcBuilders.standaloneSetup(new StudentController(service)).build();
    }

    @Test
    void validCreateReturnsCreatedAndLocation() throws Exception {
        Student created = new Student();
        created.setId(42L);
        when(service.create(any())).thenReturn(created);
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
                .content("{\"index\":\"II1/2026\",\"user\":{\"id\":1}}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/students/42"));
        verify(service).create(any());
    }

    @Test
    void invalidCreateIsRejectedBeforeCallingService() throws Exception {
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
                .content("{\"index\":\"\",\"user\":{\"id\":1}}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void missingResourceReturnsNotFound() throws Exception {
        when(service.findById(999L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        mvc.perform(get("/api/students/999")).andExpect(status().isNotFound());
    }
}
