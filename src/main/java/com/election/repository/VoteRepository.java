package com.election.repository;

import com.election.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, String> {
    List<Vote> findByConstituencyId(String constituencyId);
    List<Vote> findByVoterId(String voterId);
    long countByConstituencyId(String constituencyId);
}
