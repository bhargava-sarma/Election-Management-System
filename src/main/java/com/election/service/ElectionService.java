package com.election.service;

import com.election.dto.ElectionDTO;
import com.election.model.Election;
import com.election.model.enums.ElectionPhase;

public interface ElectionService {
    void createElection(ElectionDTO dto);
    void changePhase(String electionId, ElectionPhase phase);
    Election getActiveElection();
    boolean isVotingOpen();
}
