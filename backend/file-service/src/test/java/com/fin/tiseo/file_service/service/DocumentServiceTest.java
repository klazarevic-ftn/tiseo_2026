package com.fin.tiseo.file_service.service;

import com.fin.tiseo.file_service.entity.Document;
import com.fin.tiseo.file_service.repository.DocumentRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DocumentServiceTest {
    @Test
    void updatesExistingRecordAndKeepsItsId() {
        DocumentRepository repository = mock(DocumentRepository.class);
        Document existing = new Document();
        existing.setId(4L);
        when(repository.findById(4L)).thenReturn(Optional.of(existing));
        when(repository.saveAndFlush(any())).thenAnswer(call -> call.getArgument(0));
        Document request = new Document();
        request.setId(999L);
        request.setUserId(12L);
        request.setFileName("Diploma.pdf");
        request.setObjectKey("users/12/diploma.pdf");
        Document result = new DocumentService(repository).update(4L, request);
        assertEquals(4L, result.getId());
        assertEquals(12L, result.getUserId());
        assertEquals("Diploma.pdf", result.getFileName());
        assertEquals("users/12/diploma.pdf", result.getObjectKey());
    }

    @Test
    void missingDocumentReturnsNotFound() {
        DocumentRepository repository = mock(DocumentRepository.class);
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> new DocumentService(repository).findById(999L));
        assertEquals(404, error.getStatusCode().value());
    }

    @Test
    void documentsCanBeListedByUserRegardlessOfStudyRecord() {
        DocumentRepository repository = mock(DocumentRepository.class);
        when(repository.findByUserId(12L)).thenReturn(List.of(new Document()));
        assertEquals(1, new DocumentService(repository).findAll(12L).size());
        verify(repository, never()).findAll();
    }
}
