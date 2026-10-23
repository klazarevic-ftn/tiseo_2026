package com.fin.tiseo.payment_service.repository;

import com.fin.tiseo.payment_service.entity.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Override
    @EntityGraph(attributePaths = {"transaction", "transaction.studentAccount"})
    List<Payment> findAll();

    @Override
    @EntityGraph(attributePaths = {"transaction", "transaction.studentAccount"})
    Optional<Payment> findById(Long id);

    @EntityGraph(attributePaths = {"transaction", "transaction.studentAccount"})
    List<Payment> findByStudentId(Long id);
}
