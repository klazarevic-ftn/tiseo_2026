package com.fin.tiseo.file_service.controller;

import com.fin.tiseo.file_service.entity.Document;
import com.fin.tiseo.file_service.service.DocumentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService service;

    @GetMapping
    public List<Document> findAll(
            @RequestParam(required = false) Long userId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return service.findAll(userId, jwt);
    }

    @GetMapping("/{id}")
    public Document findById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return service.findById(id, jwt);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Document> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Long userId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Document document = service.upload(file, userId, jwt);

        return ResponseEntity
                .created(URI.create("/api/documents/" + document.getId()))
                .body(document);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Document document = service.findById(id, jwt);

        String disposition = ContentDisposition.attachment()
                .filename(document.getFileName(), StandardCharsets.UTF_8)
                .build()
                .toString();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
                .body(service.download(document));
    }

    @PutMapping("/{id}")
    public Document update(
            @PathVariable Long id,
            @Valid @RequestBody DocumentUpdate request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return service.update(id, request.fileName(), jwt);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        service.delete(id, jwt);
        return ResponseEntity.noContent().build();
    }

    public record DocumentUpdate(
            @NotBlank @Size(max = 255) String fileName
    ) {
    }
}