package com.election.model;

import com.election.model.enums.ReportType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    private String reportId;

    @Column(nullable = false)
    private String generatedByAdminId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date generatedAt;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType;
}
