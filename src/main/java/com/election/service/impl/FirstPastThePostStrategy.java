package com.election.service.impl;

import com.election.model.Vote;
import com.election.service.VoteCountingStrategy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Open/Closed Principle (OCP):
 * First-Past-The-Post counting strategy — the candidate with
 * the highest number of votes wins. Additional strategies
 * (e.g. RankedChoiceStrategy) can be created alongside this
 * class without modifying it.
 */
@Component
public class FirstPastThePostStrategy implements VoteCountingStrategy {

    @Override
    public Map<String, Integer> countVotes(List<Vote> votes) {
        Map<String, Integer> results = new HashMap<>();
        for (Vote vote : votes) {
            results.merge(vote.getCandidateId(), 1, Integer::sum);
        }
        return results;
    }
}
