package com.fin.tiseo.user_service.repository;

import com.fin.tiseo.user_service.entity.Administrator;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    @Override
    @EntityGraph(attributePaths = "user")
    List<Administrator> findAll();

    @Override
    @EntityGraph(attributePaths = "user")
    Optional<Administrator> findById(Long id);

    @EntityGraph(attributePaths = "user")
    Optional<Administrator> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
