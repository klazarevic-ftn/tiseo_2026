package com.fin.tiseo.user_service.service;

import com.fin.tiseo.user_service.entity.Administrator;
import com.fin.tiseo.user_service.entity.Student;
import com.fin.tiseo.user_service.entity.TeachingStaff;
import com.fin.tiseo.user_service.entity.User;
import com.fin.tiseo.user_service.repository.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository repository;
    private final StudentRepository studentRepository;
    private final TeachingStaffRepository teachingStaffRepository;
    private final AdministratorRepository administratorRepository;
    private final KeycloakService keycloak;
    private final PlatformTransactionManager transactionManager;

    public Profile currentProfile(String subject) {
        User user = repository
                .findByKeycloakSubjectAndDeletedFalse(subject)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND));

        return toProfile(user);
    }

    public Profile profile(Long id) {
        User user = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND));

        return toProfile(user);
    }

    public List<Profile> findProfiles(String search) {
        String text = search.trim();

        return repository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        text, text, text
                )
                .stream()
                .map(this::toProfile)
                .toList();
    }

    @Transactional
    public Profile updateCurrentContact(String subject, String email) {
        User linkedUser = repository
                .findByKeycloakSubjectAndDeletedFalse(subject)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND));

        User user = findForUpdate(linkedUser.getId());
        user.setEmail(email);

        keycloak.update(user);
        repository.saveAndFlush(user);

        return toProfile(user);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Profile createProfile(CreateRequest request) {
        if (request.user().getId() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A new user must not have an ID"
            );
        }

        if (request.type() == UserType.STUDENT) {
            checkIndex(request.index());
        }

        String subject = keycloak.create(
                request.user(),
                request.username(),
                request.password(),
                request.type().name()
        );

        TransactionTemplate transaction =
                new TransactionTemplate(transactionManager);

        try {
            return transaction.execute(status -> {
                User user = new User();
                copyFields(user, request.user());
                user.setKeycloakSubject(subject);

                user = repository.saveAndFlush(user);

                switch (request.type()) {
                    case STUDENT -> {
                        Student student = new Student();
                        student.setUser(user);
                        student.setIndex(request.index().trim());
                        studentRepository.saveAndFlush(student);
                    }

                    case STAFF -> {
                        TeachingStaff staff = new TeachingStaff();
                        staff.setUser(user);
                        teachingStaffRepository.saveAndFlush(staff);
                    }

                    case ADMINISTRATOR -> {
                        Administrator administrator = new Administrator();
                        administrator.setUser(user);
                        administratorRepository.saveAndFlush(administrator);
                    }
                }

                return toProfile(user);
            });
        } catch (RuntimeException error) {
            try {
                keycloak.removeCreatedAccount(subject);
            } catch (RuntimeException cleanupError) {
                error.addSuppressed(cleanupError);
            }

            throw error;
        }
    }

    @Transactional
    public Profile updateProfile(Long id, UpdateRequest request) {
        User user = findForUpdate(id);
        Student student = null;

        if (request.studentId() != null) {
            checkIndex(request.index());

            student = studentRepository.findById(request.studentId())
                    .filter(record -> record.getUser().getId().equals(id))
                    .orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.NOT_FOUND));
        } else if (request.index() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provide the student record ID when changing an index"
            );
        }

        copyFields(user, request.user());

        if (user.getKeycloakSubject() != null) {
            keycloak.update(user);
        }

        if (student != null) {
            student.setIndex(request.index().trim());
            studentRepository.saveAndFlush(student);
        }

        repository.saveAndFlush(user);
        return toProfile(user);
    }

    @Transactional
    public void deleteProfile(Long id) {
        User user = findForUpdate(id);

        if (user.getKeycloakSubject() != null) {
            keycloak.disable(user.getKeycloakSubject());
        }

        user.setDeleted(true);
        repository.saveAndFlush(user);
    }

    private User findForUpdate(Long id) {
        return repository.findWithLockByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private Profile toProfile(User user) {
        Long id = user.getId();

        return new Profile(
                user,
                studentRepository.findByUserId(id),
                teachingStaffRepository.findByUserId(id).orElse(null),
                administratorRepository.findByUserId(id).orElse(null)
        );
    }

    private void copyFields(User user, User request) {
        user.setUci(request.getUci());
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
    }

    private void checkIndex(String index) {
        if (index == null || index.isBlank() || index.length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provide a valid student index"
            );
        }
    }

    public enum UserType {
        STUDENT,
        STAFF,
        ADMINISTRATOR
    }

    public record CreateRequest(
            @NotNull @Valid User user,
            @NotNull UserType type,
            String index,
            @NotBlank String username,
            @NotBlank String password
    ) {
    }

    public record UpdateRequest(
            @NotNull @Valid User user,
            Long studentId,
            String index
    ) {
    }

    public record Profile(
            User user,
            List<Student> students,
            TeachingStaff staff,
            Administrator administrator
    ) {
    }
}