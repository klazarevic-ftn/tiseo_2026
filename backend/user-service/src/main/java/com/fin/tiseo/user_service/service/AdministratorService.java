package com.fin.tiseo.user_service.service;

import com.fin.tiseo.user_service.entity.Administrator;
import com.fin.tiseo.user_service.entity.User;
import com.fin.tiseo.user_service.repository.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdministratorService {
    private final AdministratorRepository repository;
    private final UserRepository userRepository;
    private final TeachingStaffRepository teachingStaffRepository;
    private final StudentRepository studentRepository;

    public List<Administrator> findAll(Long userId) {
        return userId == null ? repository.findAll() : repository.findByUserId(userId).map(List::of).orElseGet(List::of);
    }

    public Administrator findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Administrator not found"));
    }

    @Transactional
    public Administrator create(Administrator request) {
        if (request.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new record must not have an ID");
        }
        User user = resolveUser(request, null);
        Administrator record = new Administrator();
        record.setUser(user);
        return repository.saveAndFlush(record);
    }

    @Transactional
    public Administrator update(Long id, Administrator request) {
        Administrator record = findById(id);
        record.setUser(resolveUser(request, id));
        return repository.saveAndFlush(record);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
        repository.flush();
    }

    private User resolveUser(Administrator request, Long currentId) {
        if (request.getUser() == null || request.getUser().getId() == null || request.getUser().getId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An existing user ID is required");
        }
        User user = userRepository.findByIdForUpdate(request.getUser().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (studentRepository.existsByUserId(user.getId()) || teachingStaffRepository.existsByUserId(user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An administrator cannot have student or teaching affiliations");
        }
        repository.findByUserId(user.getId()).ifPresent(existing -> {
            if (!existing.getId().equals(currentId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "This user is already an administrator");
            }
        });
        return user;
    }
}
