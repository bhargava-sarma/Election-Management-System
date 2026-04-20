package com.election.dto;

import com.election.model.enums.ReportType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {

    @NotNull(message = "Report type is required")
    private ReportType reportType;
}
