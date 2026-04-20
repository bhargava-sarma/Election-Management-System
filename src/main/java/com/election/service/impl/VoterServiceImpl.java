package com.election.service.impl;

import com.election.dto.VoterDTO;
import com.election.model.Constituency;
import com.election.model.Vote;
import com.election.model.Voter;
import com.election.model.Candidate;
import com.election.model.enums.Role;
import com.election.repository.CandidateRepository;
import com.election.repository.ConstituencyRepository;
import com.election.repository.VoteRepository;
import com.election.repository.VoterRepository;
import com.election.service.ElectionService;
import com.election.service.VoterService;
import com.election.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
public class VoterServiceImpl implements VoterService {

    private final VoterRepository voterRepository;
    private final ConstituencyRepository constituencyRepository;
    private final VoteRepository voteRepository;
    private final CandidateRepository candidateRepository;
    private final ElectionService electionService;

    public VoterServiceImpl(VoterRepository voterRepository,
                            ConstituencyRepository constituencyRepository,
                            VoteRepository voteRepository,
                            CandidateRepository candidateRepository,
                            ElectionService electionService) {
        this.voterRepository = voterRepository;
        this.constituencyRepository = constituencyRepository;
        this.voteRepository = voteRepository;
        this.candidateRepository = candidateRepository;
        this.electionService = electionService;
    }

    @Override
    @Transactional
    public void registerVoter(VoterDTO dto) {
        if (voterRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        if (voterRepository.existsByAadhaarNumber(dto.getAadhaarNumber())) {
            throw new RuntimeException("Aadhaar number already registered");
        }

        Constituency constituency = constituencyRepository.findById(dto.getConstituencyId())
                .orElseThrow(() -> new RuntimeException("Constituency not found"));

        Voter voter = new Voter();
        voter.setUserId(UUID.randomUUID().toString());
        voter.setVoterId("VTR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        voter.setName(dto.getName());
        voter.setEmail(dto.getEmail());
        voter.setPasswordHash(PasswordUtil.hashPassword(dto.getPassword()));
        voter.setRole(Role.VOTER);
        voter.setAadhaarNumber(dto.getAadhaarNumber());
        voter.setHasVoted(false);
        voter.setConstituency(constituency);

        // Update the constituency's registered voter count
        constituency.setTotalVoters(constituency.getTotalVoters() + 1);
        constituencyRepository.save(constituency);

        voterRepository.save(voter);
    }

    @Override
    public Voter login(String email, String password) {
        Voter voter = voterRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!PasswordUtil.matches(password, voter.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }
        return voter;
    }

    @Override
    @Transactional
    public Vote castVote(String voterId, String candidateId) {
        if (!electionService.isVotingOpen()) {
            throw new RuntimeException("Voting is not currently open");
        }

        Voter voter = voterRepository.findById(voterId)
                .orElseThrow(() -> new RuntimeException("Voter not found"));

        if (voter.isHasVoted()) {
            throw new RuntimeException("You have already voted");
        }

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        if (!voter.getConstituency().getConstituencyId()
                .equals(candidate.getConstituency().getConstituencyId())) {
            throw new RuntimeException("Candidate is not in your constituency");
        }

        Vote vote = new Vote();
        vote.setVoteId(UUID.randomUUID().toString());
        vote.setVoterId(voterId);
        vote.setCandidateId(candidateId);
        vote.setConstituencyId(voter.getConstituency().getConstituencyId());
        vote.setTimestamp(new Date());

        voter.setHasVoted(true);
        voterRepository.save(voter);

        return voteRepository.save(vote);
    }

    @Override
    public String getVoterStatus(String voterId) {
        Voter voter = voterRepository.findById(voterId)
                .orElseThrow(() -> new RuntimeException("Voter not found"));
        return voter.isHasVoted() ? "VOTED" : "NOT_VOTED";
    }
}
