package com.election.repository;

import com.election.model.ReturningOfficer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReturningOfficerRepository extends JpaRepository<ReturningOfficer, String> {
    Optional<ReturningOfficer> findByEmail(String email);
    boolean existsByEmail(String email);
}
