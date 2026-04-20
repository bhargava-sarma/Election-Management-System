package com.election.service.impl;

import com.election.dto.CandidateDTO;
import com.election.model.Candidate;
import com.election.model.Constituency;
import com.election.model.enums.NominationStatus;
import com.election.model.enums.Role;
import com.election.repository.CandidateRepository;
import com.election.repository.ConstituencyRepository;
import com.election.service.CandidateService;
import com.election.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final ConstituencyRepository constituencyRepository;

    public CandidateServiceImpl(CandidateRepository candidateRepository,
                                ConstituencyRepository constituencyRepository) {
        this.candidateRepository = candidateRepository;
        this.constituencyRepository = constituencyRepository;
    }

    @Override
    @Transactional
    public void registerNomination(CandidateDTO dto) {
        if (candidateRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        Constituency constituency = constituencyRepository.findById(dto.getConstituencyId())
                .orElseThrow(() -> new RuntimeException("Constituency not found"));

        Candidate candidate = new Candidate();
        candidate.setUserId(UUID.randomUUID().toString());
        candidate.setCandidateId("CND-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        candidate.setName(dto.getName());
        candidate.setEmail(dto.getEmail());
        candidate.setPasswordHash(PasswordUtil.hashPassword(dto.getPassword()));
        candidate.setRole(Role.CANDIDATE);
        candidate.setPartyName(dto.getPartyName());
        candidate.setSymbol(dto.getSymbol());
        candidate.setNominationStatus(NominationStatus.PENDING);
        candidate.setConstituency(constituency);

        candidateRepository.save(candidate);
    }

    @Override
    public List<Candidate> getConstituencyCandidates(String constituencyId) {
        Constituency constituency = constituencyRepository.findById(constituencyId)
                .orElseThrow(() -> new RuntimeException("Constituency not found"));
        return candidateRepository.findByConstituency(constituency);
    }

    @Override
    @Transactional
    public void approveCandidate(String candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
        candidate.setNominationStatus(NominationStatus.APPROVED);
        candidateRepository.save(candidate);
    }

    @Override
    @Transactional
    public void rejectCandidate(String candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
        candidate.setNominationStatus(NominationStatus.REJECTED);
        candidateRepository.save(candidate);
    }
}
