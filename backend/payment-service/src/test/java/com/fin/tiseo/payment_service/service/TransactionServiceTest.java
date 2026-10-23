package com.fin.tiseo.payment_service.service;

import com.fin.tiseo.payment_service.entity.*;
import com.fin.tiseo.payment_service.repository.*;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {
    private TransactionRepository transactions;
    private StudentAccountRepository accounts;
    private TransactionService service;

    @BeforeEach
    void setUp() {
        transactions = mock(TransactionRepository.class);
        accounts = mock(StudentAccountRepository.class);
        service = new TransactionService(transactions, accounts);
    }

    @Test
    void spendingLocksAccountAndSubtractsFromBalance() {
        StudentAccount account = account("500.00");
        when(accounts.findByIdForUpdate(7L)).thenReturn(Optional.of(account));
        when(transactions.saveAndFlush(any(Transaction.class))).thenAnswer(call -> call.getArgument(0));
        Transaction result = service.create(request("-200.00"));
        assertEquals(new BigDecimal("300.00"), account.getBalance());
        assertSame(account, result.getStudentAccount());
        assertEquals(new BigDecimal("-200.00"), result.getAmount());
        assertNotNull(result.getCreatedAt());
        verify(accounts).save(account);
    }

    @Test
    void exactBalanceCanBeSpent() {
        StudentAccount account = account("200.00");
        when(accounts.findByIdForUpdate(7L)).thenReturn(Optional.of(account));
        service.create(request("-200.00"));
        assertEquals(0, account.getBalance().signum());
    }

    @Test
    void insufficientFundsChangeNeitherBalanceNorLedger() {
        StudentAccount account = account("100.00");
        when(accounts.findByIdForUpdate(7L)).thenReturn(Optional.of(account));
        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> service.create(request("-200.00")));
        assertEquals(409, error.getStatusCode().value());
        assertEquals(new BigDecimal("100.00"), account.getBalance());
        verify(accounts, never()).save(any());
        verifyNoInteractions(transactions);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "200", "-0.001"})
    void incomingMoneyCannotBePostedThroughSpendingEndpoint(String amount) {
        assertThrows(ResponseStatusException.class, () -> service.create(request(amount)));
        verifyNoInteractions(accounts, transactions);
    }

    private StudentAccount account(String balance) {
        StudentAccount account = new StudentAccount();
        account.setId(7L);
        account.setStudentId(12L);
        account.setBalance(new BigDecimal(balance));
        return account;
    }

    private Transaction request(String amount) {
        Transaction request = new Transaction();
        request.setStudentAccount(account("999999"));
        request.setAmount(new BigDecimal(amount));
        request.setPurpose("Prijava ispita");
        return request;
    }
}
