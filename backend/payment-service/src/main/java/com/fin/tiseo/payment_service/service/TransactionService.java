package com.fin.tiseo.payment_service.service;

import com.fin.tiseo.payment_service.entity.StudentAccount;
import com.fin.tiseo.payment_service.entity.Transaction;
import com.fin.tiseo.payment_service.repository.StudentAccountRepository;
import com.fin.tiseo.payment_service.repository.TransactionRepository;
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
public class TransactionService {
    private final TransactionRepository repository;
    private final StudentAccountRepository accountRepository;

    public List<Transaction> findAll(Long studentAccountId) {
        return studentAccountId == null ? repository.findAll()
                : repository.findByStudentAccountId(studentAccountId);
    }

    public Transaction findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
    }

    @Transactional
    public Transaction create(Transaction request) {
        if (request.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new transaction must not have an ID");
        }
        BigDecimal amount = request.getAmount();
        if (amount == null || amount.signum() >= 0 || amount.scale() > 2 || amount.precision() - amount.scale() > 17) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Spending must have a negative amount with at most two decimals; record incoming funds as a payment");
        }
        if (request.getPurpose() == null || request.getPurpose().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A transaction purpose is required");
        }
        if (request.getStudentAccount() == null || request.getStudentAccount().getId() == null
                || request.getStudentAccount().getId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A student account ID is required");
        }
        StudentAccount account = accountRepository.findByIdForUpdate(request.getStudentAccount().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student account not found"));
        BigDecimal balance = account.getBalance().add(amount);
        if (balance.signum() < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient funds");
        }
        Transaction transaction = new Transaction();
        transaction.setStudentAccount(account);
        transaction.setPurpose(request.getPurpose());
        transaction.setAmount(amount);
        transaction.setCreatedAt(OffsetDateTime.now());
        account.setBalance(balance);
        accountRepository.save(account);
        return repository.saveAndFlush(transaction);
    }
}
