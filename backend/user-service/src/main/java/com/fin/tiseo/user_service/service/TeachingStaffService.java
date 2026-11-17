package com.fin.tiseo.user_service.service;

import com.fin.tiseo.user_service.entity.TeachingStaff;
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
public class TeachingStaffService {
    private final TeachingStaffRepository repository;
    private final UserRepository userRepository;
    private final AdministratorRepository administratorRepository;

    public List<TeachingStaff> findAll(Long userId) {
        return userId == null ? repository.findAll() : repository.findByUserId(userId).map(List::of).orElseGet(List::of);
    }

    public TeachingStaff findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TeachingStaff not found"));
    }

    @Transactional
    public TeachingStaff create(TeachingStaff request) {
        if (request.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new record must not have an ID");
        }
        User user = resolveUser(request, null);
        TeachingStaff record = new TeachingStaff();
        record.setUser(user);
        return repository.saveAndFlush(record);
    }

    @Transactional
    public TeachingStaff update(Long id, TeachingStaff request) {
        TeachingStaff record = findById(id);
        record.setUser(resolveUser(request, id));
        return repository.saveAndFlush(record);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
        repository.flush();
    }

    private User resolveUser(TeachingStaff request, Long currentId) {
        if (request.getUser() == null || request.getUser().getId() == null || request.getUser().getId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An existing user ID is required");
        }
        User user = userRepository.findWithLockByIdAndDeletedFalse(request.getUser().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (administratorRepository.existsByUserId(user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An administrator cannot also be teaching staff");
        }
        repository.findByUserId(user.getId()).ifPresent(existing -> {
            if (!existing.getId().equals(currentId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "This user already has a teaching staff record");
            }
        });
        return user;
    }
}
