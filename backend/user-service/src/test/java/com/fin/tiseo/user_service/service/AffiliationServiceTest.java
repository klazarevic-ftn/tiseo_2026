package com.fin.tiseo.user_service.service;

import com.fin.tiseo.user_service.entity.*;
import com.fin.tiseo.user_service.repository.*;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AffiliationServiceTest {
    private UserRepository users;
    private StudentRepository students;
    private TeachingStaffRepository teachers;
    private AdministratorRepository admins;
    private User user;

    @BeforeEach
    void setUp() {
        users = mock(UserRepository.class);
        students = mock(StudentRepository.class);
        teachers = mock(TeachingStaffRepository.class);
        admins = mock(AdministratorRepository.class);
        user = new User();
        user.setId(1L);
        user.setFirstName("Existing");
        when(users.findByIdForUpdate(1L)).thenReturn(Optional.of(user));
    }

    @Test
    void studentMayAlsoBecomeTeachingStaff() {
        when(students.existsByUserId(1L)).thenReturn(true);
        when(teachers.saveAndFlush(any())).thenAnswer(call -> call.getArgument(0));
        TeachingStaff request = new TeachingStaff();
        request.setUser(user);
        TeachingStaff created = new TeachingStaffService(teachers, users, admins).create(request);
        assertSame(user, created.getUser());
    }

    @Test
    void teachingStaffMayAlsoBecomeStudent() {
        when(students.saveAndFlush(any())).thenAnswer(call -> call.getArgument(0));
        Student request = new Student();
        request.setUser(user);
        request.setIndex("II1/2026");
        Student created = new StudentService(students, users, admins).create(request);
        assertSame(user, created.getUser());
        assertEquals("II1/2026", created.getIndex());
    }

    @Test
    void multipleStudyRecordsCanBelongToSamePerson() {
        when(students.saveAndFlush(any())).thenAnswer(call -> call.getArgument(0));
        StudentService service = new StudentService(students, users, admins);
        Student bachelor = new Student();
        bachelor.setUser(user);
        bachelor.setIndex("II1/2022");
        Student master = new Student();
        master.setUser(user);
        master.setIndex("II1/2026");
        assertSame(service.create(bachelor).getUser(), service.create(master).getUser());
    }

    @Test
    void adminCannotBecomeStudent() {
        when(admins.existsByUserId(1L)).thenReturn(true);
        Student request = new Student();
        request.setUser(user);
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> new StudentService(students, users, admins).create(request));
        assertEquals(409, error.getStatusCode().value());
        verify(students, never()).saveAndFlush(any());
    }

    @Test
    void adminCannotBecomeTeacher() {
        when(admins.existsByUserId(1L)).thenReturn(true);
        TeachingStaff request = new TeachingStaff();
        request.setUser(user);
        assertThrows(ResponseStatusException.class,
                () -> new TeachingStaffService(teachers, users, admins).create(request));
        verify(teachers, never()).saveAndFlush(any());
    }

    @Test
    void studentCannotBecomeAdmin() {
        when(students.existsByUserId(1L)).thenReturn(true);
        Administrator request = new Administrator();
        request.setUser(user);
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> new AdministratorService(admins, users, teachers, students).create(request));
        assertEquals(409, error.getStatusCode().value());
        verify(admins, never()).saveAndFlush(any());
    }

    @Test
    void teacherCannotBecomeAdmin() {
        when(teachers.existsByUserId(1L)).thenReturn(true);
        Administrator request = new Administrator();
        request.setUser(user);
        assertThrows(ResponseStatusException.class,
                () -> new AdministratorService(admins, users, teachers, students).create(request));
    }

    @Test
    void affiliationUsesStoredUserInsteadOfNestedClientData() {
        when(students.saveAndFlush(any())).thenAnswer(call -> call.getArgument(0));
        User supplied = new User();
        supplied.setId(1L);
        supplied.setFirstName("Changed by nested request");
        Student request = new Student();
        request.setUser(supplied);
        Student created = new StudentService(students, users, admins).create(request);
        assertSame(user, created.getUser());
        assertEquals("Existing", user.getFirstName());
    }

    @Test
    void userCannotBeDeletedWhileAffiliationsRemain() {
        when(students.existsByUserId(1L)).thenReturn(true);
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> new UserService(users, students, teachers, admins).delete(1L));
        assertEquals(409, error.getStatusCode().value());
        verify(users, never()).delete(any());
    }
}
