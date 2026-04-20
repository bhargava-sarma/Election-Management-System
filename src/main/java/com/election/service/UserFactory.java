package com.election.service;

import com.election.model.*;
import com.election.model.enums.Role;

/**
 * Factory Pattern (Creational):
 * Creates User subclass instances based on the given Role enum value.
 * Centralises object creation logic so callers do not need to know
 * the concrete class that corresponds to each role.
 */
public class UserFactory {

    private UserFactory() {
        // Utility class — prevent instantiation
    }

    /**
     * Creates and returns a new User subclass instance matching the given role.
     *
     * @param role   the Role enum value
     * @param userId unique identifier for the user
     * @param name   display name
     * @param email  email address
     * @param passwordHash  hashed password
     * @return a concrete User subclass (Voter, Candidate, ReturningOfficer, or Admin)
     * @throws IllegalArgumentException if the role is unrecognised
     */
    public static User createUser(Role role, String userId, String name,
                                  String email, String passwordHash) {
        switch (role) {
            case VOTER:
                Voter voter = new Voter();
                voter.setUserId(userId);
                voter.setName(name);
                voter.setEmail(email);
                voter.setPasswordHash(passwordHash);
                voter.setRole(Role.VOTER);
                return voter;

            case CANDIDATE:
                Candidate candidate = new Candidate();
                candidate.setUserId(userId);
                candidate.setName(name);
                candidate.setEmail(email);
                candidate.setPasswordHash(passwordHash);
                candidate.setRole(Role.CANDIDATE);
                return candidate;

            case RETURNING_OFFICER:
                ReturningOfficer officer = new ReturningOfficer();
                officer.setUserId(userId);
                officer.setName(name);
                officer.setEmail(email);
                officer.setPasswordHash(passwordHash);
                officer.setRole(Role.RETURNING_OFFICER);
                return officer;

            case ADMIN:
                Admin admin = new Admin();
                admin.setUserId(userId);
                admin.setName(name);
                admin.setEmail(email);
                admin.setPasswordHash(passwordHash);
                admin.setRole(Role.ADMIN);
                return admin;

            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }
}
