package com.fin.tiseo.payment_service.controller;

import com.fin.tiseo.payment_service.entity.Payment;
import com.fin.tiseo.payment_service.service.PaymentService;
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

class PaymentControllerTest {
    private PaymentService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(PaymentService.class);
        mvc = MockMvcBuilders.standaloneSetup(new PaymentController(service)).build();
    }

    @Test
    void validCreateReturnsCreatedAndLocation() throws Exception {
        Payment created = new Payment();
        created.setId(42L);
        when(service.create(any())).thenReturn(created);
        mvc.perform(post("/api/payments").contentType(MediaType.APPLICATION_JSON)
                .content("{\"studentId\":12,\"amount\":200}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/payments/42"));
        verify(service).create(any());
    }

    @Test
    void invalidCreateIsRejectedBeforeCallingService() throws Exception {
        mvc.perform(post("/api/payments").contentType(MediaType.APPLICATION_JSON)
                .content("{\"studentId\":12,\"amount\":-200}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void missingResourceReturnsNotFound() throws Exception {
        when(service.findById(999L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        mvc.perform(get("/api/payments/999")).andExpect(status().isNotFound());
    }
}
