package com.election.service;

import com.election.model.Vote;
import org.springframework.stereotype.Service;

/**
 * Facade Pattern (Structural):
 * Provides a simplified interface for the voting process.
 * Internally delegates to VoterService and ElectionService,
 * hiding the complexity from calling controllers.
 * Controllers only need to call votingFacade.castVote(...)
 * instead of coordinating multiple services themselves.
 */
@Service
public class VotingFacade {

    private final VoterService voterService;
    private final ElectionService electionService;

    public VotingFacade(VoterService voterService,
                        ElectionService electionService) {
        this.voterService = voterService;
        this.electionService = electionService;
    }

    /**
     * Single entry point for casting a vote.
     * Validates that voting is open, then delegates to VoterService
     * for the actual vote recording.
     *
     * @param voterId     the ID of the voter
     * @param candidateId the ID of the candidate being voted for
     * @return the recorded Vote
     * @throws RuntimeException if voting is not open or vote cannot be cast
     */
    public Vote castVote(String voterId, String candidateId) {
        if (!electionService.isVotingOpen()) {
            throw new RuntimeException("Voting is not currently open");
        }
        return voterService.castVote(voterId, candidateId);
    }

    /**
     * Checks whether voting is currently open.
     *
     * @return true if an active election is in the VOTING phase
     */
    public boolean isVotingOpen() {
        return electionService.isVotingOpen();
    }

    /**
     * Returns the voting status for a given voter.
     *
     * @param voterId the voter's ID
     * @return "VOTED" or "NOT_VOTED"
     */
    public String getVoterStatus(String voterId) {
        return voterService.getVoterStatus(voterId);
    }
}
