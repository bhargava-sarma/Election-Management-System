package com.election.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "voters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Voter extends User {

    @Column(nullable = false)
    private String voterId;

    @Column(nullable = false, unique = true)
    private String aadhaarNumber;

    @Column(nullable = false)
    private boolean hasVoted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "constituency_id")
    private Constituency constituency;
}
