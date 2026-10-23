package com.fin.tiseo.file_service.service;

import com.fin.tiseo.file_service.entity.Document;
import com.fin.tiseo.file_service.repository.DocumentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentService {
    private final DocumentRepository repository;

    public List<Document> findAll(Long userId) {
        return userId == null ? repository.findAll() : repository.findByUserId(userId);
    }

    public Document findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
    }

    @Transactional
    public Document create(Document request) {
        if (request.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new document must not have an ID");
        }
        Document document = new Document();
        copyFields(document, request);
        return repository.saveAndFlush(document);
    }

    @Transactional
    public Document update(Long id, Document request) {
        Document document = findById(id);
        copyFields(document, request);
        return repository.saveAndFlush(document);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
        repository.flush();
    }

    private void copyFields(Document document, Document request) {
        document.setUserId(request.getUserId());
        document.setFileName(request.getFileName());
        document.setObjectKey(request.getObjectKey());
    }
}
