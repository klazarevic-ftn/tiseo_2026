package com.fin.tiseo.user_service.repository;

import com.fin.tiseo.user_service.entity.TeachingStaff;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeachingStaffRepository extends JpaRepository<TeachingStaff, Long> {
    @Override
    @EntityGraph(attributePaths = "user")
    List<TeachingStaff> findAll();

    @Override
    @EntityGraph(attributePaths = "user")
    Optional<TeachingStaff> findById(Long id);

    @EntityGraph(attributePaths = "user")
    Optional<TeachingStaff> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
