package com.fin.tiseo.payment_service.repository;

import com.fin.tiseo.payment_service.entity.StudentAccount;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentAccountRepository extends JpaRepository<StudentAccount, Long> {
    Optional<StudentAccount> findByStudentId(Long studentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from StudentAccount a where a.id = :id")
    Optional<StudentAccount> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from StudentAccount a where a.studentId = :studentId")
    Optional<StudentAccount> findByStudentIdForUpdate(@Param("studentId") Long studentId);
}
