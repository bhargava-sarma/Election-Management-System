package com.election.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "votes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vote {

    @Id
    private String voteId;

    @Column(nullable = false)
    private String voterId;

    @Column(nullable = false)
    private String candidateId;

    @Column(nullable = false)
    private String constituencyId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date timestamp;
}
