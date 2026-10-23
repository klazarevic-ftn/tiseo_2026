package com.fin.tiseo.payment_service.service;

import com.fin.tiseo.payment_service.entity.StudentAccount;
import com.fin.tiseo.payment_service.repository.StudentAccountRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StudentAccountServiceTest {
    @Test
    void initialBalanceCannotBeInjectedByClient() {
        StudentAccountRepository repository = mock(StudentAccountRepository.class);
        when(repository.saveAndFlush(any())).thenAnswer(call -> call.getArgument(0));
        StudentAccount request = new StudentAccount();
        request.setStudentId(12L);
        request.setBalance(new BigDecimal("99999"));
        StudentAccount created = new StudentAccountService(repository).create(request);
        assertEquals(new BigDecimal("0.00"), created.getBalance());
    }

    @Test
    void duplicateAccountReturnsConflict() {
        StudentAccountRepository repository = mock(StudentAccountRepository.class);
        when(repository.findByStudentId(12L)).thenReturn(Optional.of(new StudentAccount()));
        StudentAccount request = new StudentAccount();
        request.setStudentId(12L);
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> new StudentAccountService(repository).create(request));
        assertEquals(409, error.getStatusCode().value());
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void concurrentDuplicateAccountReturnsConflict() {
        StudentAccountRepository repository = mock(StudentAccountRepository.class);
        when(repository.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("duplicate student"));
        StudentAccount request = new StudentAccount();
        request.setStudentId(12L);
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> new StudentAccountService(repository).create(request));
        assertEquals(409, error.getStatusCode().value());
    }
}
