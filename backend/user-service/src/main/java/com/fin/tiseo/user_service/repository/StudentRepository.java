package com.fin.tiseo.user_service.repository;

import com.fin.tiseo.user_service.entity.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    @Override
    @EntityGraph(attributePaths = "user")
    List<Student> findAll();

    @Override
    @EntityGraph(attributePaths = "user")
    Optional<Student> findById(Long id);

    @EntityGraph(attributePaths = "user")
    List<Student> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
