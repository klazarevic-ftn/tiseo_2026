package com.fin.tiseo.file_service.repository;

import com.fin.tiseo.file_service.entity.Document;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByDeletedFalse();

    List<Document> findByUserIdAndDeletedFalse(Long userId);

    Optional<Document> findByIdAndDeletedFalse(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select d from Document d
            where d.id = :id and d.deleted = false
            """)
    Optional<Document> findActiveForUpdate(@Param("id") Long id);
}