package com.election.repository;

import com.election.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, String> {
    Optional<Result> findByConstituency_ConstituencyId(String constituencyId);
}
