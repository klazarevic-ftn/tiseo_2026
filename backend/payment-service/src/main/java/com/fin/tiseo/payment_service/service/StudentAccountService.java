package com.fin.tiseo.payment_service.service;

import com.fin.tiseo.payment_service.entity.StudentAccount;
import com.fin.tiseo.payment_service.repository.StudentAccountRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentAccountService {
    private final StudentAccountRepository repository;

    public List<StudentAccount> findAll(Long studentId) {
        return studentId == null ? repository.findAll()
                : repository.findByStudentId(studentId).map(List::of).orElseGet(List::of);
    }

    public StudentAccount findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student account not found"));
    }

    @Transactional
    public StudentAccount create(StudentAccount request) {
        if (request.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new account must not have an ID");
        }
        if (request.getStudentId() == null || request.getStudentId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A student ID is required");
        }
        if (repository.findByStudentId(request.getStudentId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This student already has an account");
        }
        StudentAccount account = new StudentAccount();
        account.setStudentId(request.getStudentId());
        account.setBalance(new BigDecimal("0.00"));
        try {
            return repository.saveAndFlush(account);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This student already has an account", exception);
        }
    }
}
