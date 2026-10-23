package com.fin.tiseo.file_service.controller;

import com.fin.tiseo.file_service.entity.Document;
import com.fin.tiseo.file_service.service.DocumentService;
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

class DocumentControllerTest {
    private DocumentService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(DocumentService.class);
        mvc = MockMvcBuilders.standaloneSetup(new DocumentController(service)).build();
    }

    @Test
    void validCreateReturnsCreatedAndLocation() throws Exception {
        Document created = new Document();
        created.setId(42L);
        when(service.create(any())).thenReturn(created);
        mvc.perform(post("/api/documents").contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":12,\"fileName\":\"Diploma.pdf\",\"objectKey\":\"12/diploma.pdf\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/documents/42"));
        verify(service).create(any());
    }

    @Test
    void invalidCreateIsRejectedBeforeCallingService() throws Exception {
        mvc.perform(post("/api/documents").contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":12,\"fileName\":\"\",\"objectKey\":\"12/diploma.pdf\"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void missingResourceReturnsNotFound() throws Exception {
        when(service.findById(999L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        mvc.perform(get("/api/documents/999")).andExpect(status().isNotFound());
    }
}
