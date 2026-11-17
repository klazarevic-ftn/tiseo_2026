package com.fin.tiseo.file_service.service;

import com.fin.tiseo.file_service.entity.Document;
import com.fin.tiseo.file_service.repository.DocumentRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {

    private final DocumentRepository repository;
    private final StorageService storage;
    private final UserClient users;

    public List<Document> findAll(Long userId, Jwt jwt) {
        UserClient.Access user = checkAccess(jwt);

        if (user.administrator()) {
            return userId == null
                    ? repository.findByDeletedFalse()
                    : repository.findByUserIdAndDeletedFalse(userId);
        }

        if (userId != null && !userId.equals(user.userId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return repository.findByUserIdAndDeletedFalse(user.userId());
    }

    public Document findById(Long id, Jwt jwt) {
        UserClient.Access user = checkAccess(jwt);

        Document document = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND));

        checkOwner(document, user);
        return document;
    }

    public Document upload(MultipartFile file, Long userId, Jwt jwt) {
        UserClient.Access user = checkAccess(jwt);
        Long ownerId = user.userId();

        if (user.administrator()) {
            if (userId == null || userId <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Choose a student");
            }

            UserClient.Access owner = users.user(userId, jwt);

            if (!owner.student()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "User is not a student");
            }

            ownerId = owner.userId();
        } else if (userId != null && !userId.equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        String fileName = StringUtils.getFilename(
                StringUtils.cleanPath(file.getOriginalFilename()));

        if (file.isEmpty() || fileName == null
                || fileName.isBlank() || fileName.length() > 255
                || file.getSize() > 10L * 1024 * 1024) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid file");
        }

        String key = "users/" + ownerId + "/" + UUID.randomUUID();
        storage.upload(key, file);

        Document document = new Document();
        document.setFileName(fileName);
        document.setObjectKey(key);
        document.setUserId(ownerId);

        return repository.saveAndFlush(document);
    }

    public byte[] download(Document document) {
        return storage.download(document.getObjectKey());
    }

    public Document update(Long id, String fileName, Jwt jwt) {
        UserClient.Access user = checkAccess(jwt);

        Document document = repository.findActiveForUpdate(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND));

        checkOwner(document, user);
        document.setFileName(fileName.trim());

        return repository.saveAndFlush(document);
    }

    public void delete(Long id, Jwt jwt) {
        UserClient.Access user = checkAccess(jwt);

        if (!user.administrator()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Document document = repository.findActiveForUpdate(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND));

        document.setDeleted(true);
        repository.saveAndFlush(document);
    }

    private UserClient.Access checkAccess(Jwt jwt) {
        UserClient.Access user = users.currentUser(jwt);

        if (!user.student() && !user.administrator()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return user;
    }

    private void checkOwner(Document document, UserClient.Access user) {
        if (!user.administrator()
                && !document.getUserId().equals(user.userId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}