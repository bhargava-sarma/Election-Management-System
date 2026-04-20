package com.election.service;

import com.election.model.enums.ElectionPhase;

/**
 * Observer Pattern (Behavioral):
 * Listener interface for election phase change events.
 * Implementations are notified whenever an election transitions
 * to a new phase.
 */
public interface ElectionPhaseListener {

    /**
     * Called when the election phase changes.
     *
     * @param electionId the ID of the election whose phase changed
     * @param newPhase   the new phase the election has transitioned to
     */
    void onPhaseChange(String electionId, ElectionPhase newPhase);
}
