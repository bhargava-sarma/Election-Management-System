package com.election.repository;

import com.election.model.Voter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoterRepository extends JpaRepository<Voter, String> {
    Optional<Voter> findByEmail(String email);
    Optional<Voter> findByVoterId(String voterId);
    Optional<Voter> findByAadhaarNumber(String aadhaarNumber);
    boolean existsByEmail(String email);
    boolean existsByAadhaarNumber(String aadhaarNumber);
}
