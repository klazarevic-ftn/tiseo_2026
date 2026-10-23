package com.fin.tiseo.user_service.service;

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
public class UserService {
    private final UserRepository repository;
    private final StudentRepository studentRepository;
    private final TeachingStaffRepository teachingStaffRepository;
    private final AdministratorRepository administratorRepository;

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Transactional
    public User create(User request) {
        if (request.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new user must not have an ID");
        }
        User user = new User();
        copyFields(user, request);
        return repository.saveAndFlush(user);
    }

    @Transactional
    public User update(Long id, User request) {
        User user = findForUpdate(id);
        copyFields(user, request);
        return repository.saveAndFlush(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = findForUpdate(id);
        if (studentRepository.existsByUserId(id) || teachingStaffRepository.existsByUserId(id)
                || administratorRepository.existsByUserId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Remove the user's affiliations first");
        }
        repository.delete(user);
        repository.flush();
    }

    private User findForUpdate(Long id) {
        return repository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private void copyFields(User user, User request) {
        user.setUci(request.getUci());
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
    }
}
