package com.election.service;

import com.election.dto.CandidateDTO;
import com.election.model.Candidate;

import java.util.List;

public interface CandidateService {
    void registerNomination(CandidateDTO dto);
    List<Candidate> getConstituencyCandidates(String constituencyId);
    void approveCandidate(String candidateId);
    void rejectCandidate(String candidateId);
}
