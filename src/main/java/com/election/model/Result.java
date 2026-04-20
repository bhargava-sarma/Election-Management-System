package com.election.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    @Id
    private String resultId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "constituency_id", unique = true)
    private Constituency constituency;

    private String winnerCandidateId;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String voteCounts;
}
