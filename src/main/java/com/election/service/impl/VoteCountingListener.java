package com.election.service.impl;

import com.election.model.enums.ElectionPhase;
import com.election.service.ElectionPhaseListener;
import com.election.service.ResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Observer that listens for election phase changes and triggers
 * automatic vote counting when the phase transitions to COUNTING.
 */
@Service
public class VoteCountingListener implements ElectionPhaseListener {

    private static final Logger logger = LoggerFactory.getLogger(VoteCountingListener.class);

    private final ResultService resultService;

    public VoteCountingListener(ResultService resultService) {
        this.resultService = resultService;
    }

    @Override
    public void onPhaseChange(String electionId, ElectionPhase newPhase) {
        if (newPhase == ElectionPhase.COUNTING) {
            logger.info("Phase changed to COUNTING for election '{}'. " +
                    "Automatically tallying votes...", electionId);
            resultService.computeResults();
            logger.info("Vote counting complete for election '{}'.", electionId);
        }
    }
}
