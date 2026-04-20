package com.election.repository;

import com.election.model.Candidate;
import com.election.model.Constituency;
import com.election.model.enums.NominationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, String> {
    Optional<Candidate> findByEmail(String email);
    Optional<Candidate> findByCandidateId(String candidateId);
    List<Candidate> findByConstituency(Constituency constituency);
    List<Candidate> findByConstituencyAndNominationStatus(Constituency constituency, NominationStatus status);
    List<Candidate> findByNominationStatus(NominationStatus status);
    boolean existsByEmail(String email);
}
