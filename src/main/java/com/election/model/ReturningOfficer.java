package com.election.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "returning_officers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReturningOfficer extends User {

    @Column(nullable = false)
    private String officerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_constituency_id")
    private Constituency assignedConstituency;
}
