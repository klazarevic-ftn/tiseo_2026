package com.fin.tiseo.payment_service.service;

import com.fin.tiseo.payment_service.entity.*;
import com.fin.tiseo.payment_service.repository.*;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {
    private PaymentRepository payments;
    private TransactionRepository transactions;
    private StudentAccountRepository accounts;
    private PaymentService service;

    @BeforeEach
    void setUp() {
        payments = mock(PaymentRepository.class);
        transactions = mock(TransactionRepository.class);
        accounts = mock(StudentAccountRepository.class);
        service = new PaymentService(payments, transactions, accounts);
    }

    @Test
    void paymentCreatesLinkedCreditAndUpdatesStoredBalance() {
        StudentAccount account = new StudentAccount();
        account.setId(7L);
        account.setStudentId(12L);
        account.setBalance(new BigDecimal("100.00"));
        when(accounts.findByStudentIdForUpdate(12L)).thenReturn(Optional.of(account));
        when(transactions.save(any(Transaction.class))).thenAnswer(call -> call.getArgument(0));
        when(payments.saveAndFlush(any(Payment.class))).thenAnswer(call -> call.getArgument(0));
        Payment request = payment("250.50");
        // Client-supplied transaction data must never replace the generated credit.
        Transaction supplied = new Transaction();
        supplied.setAmount(new BigDecimal("999999"));
        request.setTransaction(supplied);

        Payment result = service.create(request);

        assertEquals(new BigDecimal("350.50"), account.getBalance());
        assertEquals(request.getAmount(), result.getAmount());
        assertEquals(request.getAmount(), result.getTransaction().getAmount());
        assertSame(account, result.getTransaction().getStudentAccount());
        assertNotSame(supplied, result.getTransaction());
        assertNotNull(result.getReceivedAt());
        assertNotNull(result.getTransaction().getCreatedAt());
        verify(accounts).findByStudentIdForUpdate(12L);
        verify(accounts).save(account);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "0.001", "100000000000000000.00"})
    void invalidAmountsDoNotWriteAnything(String amount) {
        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> service.create(payment(amount)));
        assertEquals(400, error.getStatusCode().value());
        verifyNoInteractions(accounts, transactions, payments);
    }

    @Test
    void missingAccountDoesNotCreateLedgerEntry() {
        when(accounts.findByStudentIdForUpdate(12L)).thenReturn(Optional.empty());
        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> service.create(payment("20")));
        assertEquals(404, error.getStatusCode().value());
        verifyNoInteractions(transactions, payments);
    }

    @Test
    void creatingWithExistingIdIsRejected() {
        Payment request = payment("20");
        request.setId(1L);
        assertThrows(ResponseStatusException.class, () -> service.create(request));
        verifyNoInteractions(accounts, transactions, payments);
    }

    private Payment payment(String amount) {
        Payment request = new Payment();
        request.setStudentId(12L);
        request.setAmount(new BigDecimal(amount));
        return request;
    }
}
