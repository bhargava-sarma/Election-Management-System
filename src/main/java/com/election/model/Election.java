package com.election.model;

import com.election.model.enums.ElectionPhase;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "elections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Election {

    @Id
    private String electionId;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ElectionPhase currentPhase = ElectionPhase.SCHEDULED;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date endDate;
}
