package com.election.model;

import com.election.model.enums.NominationStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "candidates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Candidate extends User {

    @Column(nullable = false)
    private String candidateId;

    @Column(nullable = false)
    private String partyName;

    @Column(nullable = false)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NominationStatus nominationStatus = NominationStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "constituency_id")
    private Constituency constituency;
}
