package com.election.service;

import com.election.dto.VoterDTO;
import com.election.model.Vote;
import com.election.model.Voter;

public interface VoterService {
    void registerVoter(VoterDTO dto);
    Voter login(String email, String password);
    Vote castVote(String voterId, String candidateId);
    String getVoterStatus(String voterId);
}
