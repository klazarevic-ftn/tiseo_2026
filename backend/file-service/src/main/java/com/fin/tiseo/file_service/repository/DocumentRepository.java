package com.fin.tiseo.file_service.repository;

import com.fin.tiseo.file_service.entity.Document;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByUserId(Long userId);
}
