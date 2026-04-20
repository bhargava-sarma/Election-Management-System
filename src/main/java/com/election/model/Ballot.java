package com.election.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ballots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ballot {

    @Id
    private String ballotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id")
    private Election election;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "constituency_id")
    private Constituency constituency;
}
