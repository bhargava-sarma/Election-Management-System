package com.election.service.impl;

import com.election.dto.ElectionDTO;
import com.election.model.Election;
import com.election.model.enums.ElectionPhase;
import com.election.repository.ElectionRepository;
import com.election.service.ElectionPhaseListener;
import com.election.service.ElectionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ElectionServiceImpl implements ElectionService {

    private final ElectionRepository electionRepository;
    private final List<ElectionPhaseListener> phaseListeners;

    public ElectionServiceImpl(ElectionRepository electionRepository,
                               List<ElectionPhaseListener> phaseListeners) {
        this.electionRepository = electionRepository;
        this.phaseListeners = phaseListeners;
    }

    @Override
    @Transactional
    public void createElection(ElectionDTO dto) {
        Election election = new Election();
        election.setElectionId(UUID.randomUUID().toString());
        election.setTitle(dto.getTitle());
        election.setCurrentPhase(ElectionPhase.SCHEDULED);
        election.setStartDate(dto.getStartDate());
        election.setEndDate(dto.getEndDate());
        electionRepository.save(election);
    }

    @Override
    @Transactional
    public void changePhase(String electionId, ElectionPhase phase) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found"));
        election.setCurrentPhase(phase);
        electionRepository.save(election);

        // Observer Pattern: notify all registered phase listeners
        for (ElectionPhaseListener listener : phaseListeners) {
            listener.onPhaseChange(electionId, phase);
        }
    }

    @Override
    public Election getActiveElection() {
        List<Election> elections = electionRepository.findAll();
        if (elections.isEmpty()) {
            return null;
        }
        // Return the most recently created election
        return elections.get(elections.size() - 1);
    }

    @Override
    public boolean isVotingOpen() {
        Election active = getActiveElection();
        return active != null && active.getCurrentPhase() == ElectionPhase.VOTING;
    }
}
