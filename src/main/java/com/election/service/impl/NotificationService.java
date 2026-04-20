package com.election.service.impl;

import com.election.model.enums.ElectionPhase;
import com.election.service.ElectionPhaseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Observer Pattern (Behavioral):
 * Concrete observer that listens for election phase changes
 * and logs/stores notifications accordingly.
 */
@Service
public class NotificationService implements ElectionPhaseListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    /** In-memory store of notification messages for demonstration purposes. */
    private final List<String> notifications = new ArrayList<>();

    @Override
    public void onPhaseChange(String electionId, ElectionPhase newPhase) {
        String message = String.format(
                "[%s] Election '%s' phase changed to: %s",
                LocalDateTime.now(), electionId, newPhase);

        logger.info("NOTIFICATION: {}", message);
        notifications.add(message);
    }

    /**
     * Returns an unmodifiable view of all stored notifications.
     *
     * @return list of notification messages
     */
    public List<String> getNotifications() {
        return Collections.unmodifiableList(notifications);
    }
}
