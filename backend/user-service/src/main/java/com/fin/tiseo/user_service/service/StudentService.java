package com.fin.tiseo.user_service.service;

import com.fin.tiseo.user_service.entity.Student;
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
public class StudentService {
    private final StudentRepository repository;
    private final UserRepository userRepository;
    private final AdministratorRepository administratorRepository;

    public List<Student> findAll(Long userId) {
        return userId == null ? repository.findAll() : repository.findByUserId(userId);
    }

    public Student findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
    }

    @Transactional
    public Student create(Student request) {
        if (request.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new record must not have an ID");
        }
        User user = resolveUser(request, null);
        Student record = new Student();
        record.setUser(user);
        record.setIndex(request.getIndex());
        return repository.saveAndFlush(record);
    }

    @Transactional
    public Student update(Long id, Student request) {
        Student record = findById(id);
        record.setUser(resolveUser(request, id));
        record.setIndex(request.getIndex());
        return repository.saveAndFlush(record);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
        repository.flush();
    }

    private User resolveUser(Student request, Long currentId) {
        if (request.getUser() == null || request.getUser().getId() == null || request.getUser().getId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An existing user ID is required");
        }
        User user = userRepository.findWithLockByIdAndDeletedFalse(request.getUser().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (administratorRepository.existsByUserId(user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An administrator cannot also be a student");
        }
        return user;
    }

    public List<Student> findByIds(List<Long> ids) {
        return repository.findAllById(ids)
                .stream()
                .filter(student -> !student.getUser().isDeleted())
                .toList();
    }
}
