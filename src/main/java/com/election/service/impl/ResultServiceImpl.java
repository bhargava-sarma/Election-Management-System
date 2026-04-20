package com.election.service.impl;

import com.election.model.Candidate;
import com.election.model.Constituency;
import com.election.model.Result;
import com.election.model.Vote;
import com.election.repository.CandidateRepository;
import com.election.repository.ConstituencyRepository;
import com.election.repository.ResultRepository;
import com.election.repository.VoteRepository;
import com.election.service.ResultService;
import com.election.service.VoteCountingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Tallies votes per constituency using the injected VoteCountingStrategy
 * (First-Past-The-Post by default) and stores Result records.
 */
@Service
public class ResultServiceImpl implements ResultService {

    private static final Logger logger = LoggerFactory.getLogger(ResultServiceImpl.class);

    private final ResultRepository resultRepository;
    private final VoteRepository voteRepository;
    private final ConstituencyRepository constituencyRepository;
    private final CandidateRepository candidateRepository;
    private final VoteCountingStrategy countingStrategy;

    public ResultServiceImpl(ResultRepository resultRepository,
                             VoteRepository voteRepository,
                             ConstituencyRepository constituencyRepository,
                             CandidateRepository candidateRepository,
                             VoteCountingStrategy countingStrategy) {
        this.resultRepository = resultRepository;
        this.voteRepository = voteRepository;
        this.constituencyRepository = constituencyRepository;
        this.candidateRepository = candidateRepository;
        this.countingStrategy = countingStrategy;
    }

    @Override
    @Transactional
    public void computeResults() {
        List<Constituency> constituencies = constituencyRepository.findAll();

        for (Constituency constituency : constituencies) {
            // Skip if result already computed for this constituency
            if (resultRepository.findByConstituency_ConstituencyId(
                    constituency.getConstituencyId()).isPresent()) {
                logger.info("Result already exists for constituency '{}', skipping.",
                        constituency.getName());
                continue;
            }

            List<Vote> votes = voteRepository.findByConstituencyId(
                    constituency.getConstituencyId());

            if (votes.isEmpty()) {
                logger.info("No votes in constituency '{}', skipping.", constituency.getName());
                continue;
            }

            // Use the strategy pattern to count votes
            Map<String, Integer> voteCounts = countingStrategy.countVotes(votes);

            // Determine winner (candidate with most votes)
            String winnerId = voteCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

            // Build a readable vote counts string
            StringBuilder voteCountsStr = new StringBuilder();
            for (Map.Entry<String, Integer> entry : voteCounts.entrySet()) {
                String candidateName = candidateRepository.findById(entry.getKey())
                        .map(Candidate::getName).orElse(entry.getKey());
                String partyName = candidateRepository.findById(entry.getKey())
                        .map(Candidate::getPartyName).orElse("Independent");
                voteCountsStr.append(candidateName)
                        .append(" (").append(partyName).append(")")
                        .append(": ").append(entry.getValue()).append(" votes\n");
            }

            // Create and save result
            Result result = new Result();
            result.setResultId(UUID.randomUUID().toString());
            result.setConstituency(constituency);
            result.setWinnerCandidateId(winnerId);
            result.setVoteCounts(voteCountsStr.toString());

            resultRepository.save(result);

            String winnerName = candidateRepository.findById(winnerId)
                    .map(Candidate::getName).orElse("Unknown");
            logger.info("Result computed for '{}': Winner = {} with {} votes",
                    constituency.getName(), winnerName,
                    voteCounts.getOrDefault(winnerId, 0));
        }
    }

    @Override
    public List<Result> getAllResults() {
        return resultRepository.findAll();
    }
}
