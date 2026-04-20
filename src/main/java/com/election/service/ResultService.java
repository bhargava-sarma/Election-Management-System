package com.election.service;

import com.election.model.Result;

import java.util.List;

/**
 * Service for computing and retrieving election results.
 */
public interface ResultService {

    /**
     * Tallies votes for all constituencies and stores Result records.
     * Uses the configured VoteCountingStrategy.
     */
    void computeResults();

    /**
     * Returns all stored results.
     */
    List<Result> getAllResults();
}
