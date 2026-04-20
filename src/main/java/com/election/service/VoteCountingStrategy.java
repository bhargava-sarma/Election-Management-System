package com.election.service;

import com.election.model.Vote;

import java.util.List;
import java.util.Map;

/**
 * Open/Closed Principle (OCP):
 * Strategy interface for vote counting algorithms.
 * New counting strategies (e.g. ranked-choice, proportional)
 * can be added by creating new implementations without
 * modifying any existing code.
 */
public interface VoteCountingStrategy {

    /**
     * Counts votes and returns a mapping of candidate IDs to their vote totals.
     *
     * @param votes the list of votes to count
     * @return a map where the key is the candidate ID and the value is the vote count
     */
    Map<String, Integer> countVotes(List<Vote> votes);
}
