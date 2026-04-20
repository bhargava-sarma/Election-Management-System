package com.election.service;

import com.election.model.Report;
import com.election.model.enums.ReportType;

import java.util.List;

public interface ReportService {
    Report generateReport(String adminId, ReportType type);
    List<Report> getAllReports();
}
