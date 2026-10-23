package com.fin.tiseo.payment_service.service;

import com.fin.tiseo.payment_service.entity.Payment;
import com.fin.tiseo.payment_service.entity.StudentAccount;
import com.fin.tiseo.payment_service.entity.Transaction;
import com.fin.tiseo.payment_service.repository.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {
    private final PaymentRepository repository;
    private final TransactionRepository transactionRepository;
    private final StudentAccountRepository accountRepository;

    public List<Payment> findAll(Long studentId) {
        return studentId == null ? repository.findAll() : repository.findByStudentId(studentId);
    }

    public Payment findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
    }

    @Transactional
    public Payment create(Payment request) {
        if (request.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new payment must not have an ID");
        }
        BigDecimal amount = request.getAmount();
        if (amount == null || amount.signum() <= 0 || amount.scale() > 2 || amount.precision() - amount.scale() > 17) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment amount must be positive with at most two decimals");
        }
        if (request.getStudentId() == null || request.getStudentId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A student ID is required");
        }
        StudentAccount account = accountRepository.findByStudentIdForUpdate(request.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student account not found"));
        OffsetDateTime receivedAt = request.getReceivedAt() == null ? OffsetDateTime.now() : request.getReceivedAt();
        Transaction transaction = new Transaction();
        transaction.setStudentAccount(account);
        transaction.setPurpose("Uplata");
        transaction.setAmount(amount);
        transaction.setCreatedAt(OffsetDateTime.now());
        transaction = transactionRepository.save(transaction);

        Payment payment = new Payment();
        payment.setStudentId(account.getStudentId());
        payment.setAmount(amount);
        payment.setReceivedAt(receivedAt);
        payment.setTransaction(transaction);
        account.setBalance(account.getBalance().add(amount));
        if (account.getBalance().precision() - account.getBalance().scale() > 17) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Account balance exceeds the supported limit");
        }
        accountRepository.save(account);
        return repository.saveAndFlush(payment);
    }
}
