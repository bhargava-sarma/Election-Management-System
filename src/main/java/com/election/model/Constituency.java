package com.election.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "constituencies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Constituency {

    @Id
    private String constituencyId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private int totalVoters;
}
