package com.election.service.impl;

import com.election.model.*;
import com.election.model.enums.NominationStatus;
import com.election.model.enums.ReportType;
import com.election.repository.*;
import com.election.service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final VoteRepository voteRepository;
    private final ConstituencyRepository constituencyRepository;
    private final VoterRepository voterRepository;
    private final CandidateRepository candidateRepository;

    public ReportServiceImpl(ReportRepository reportRepository,
                             VoteRepository voteRepository,
                             ConstituencyRepository constituencyRepository,
                             VoterRepository voterRepository,
                             CandidateRepository candidateRepository) {
        this.reportRepository = reportRepository;
        this.voteRepository = voteRepository;
        this.constituencyRepository = constituencyRepository;
        this.voterRepository = voterRepository;
        this.candidateRepository = candidateRepository;
    }

    @Override
    @Transactional
    public Report generateReport(String adminId, ReportType type) {
        String content;
        switch (type) {
            case RESULTS:
                content = generateResultsReport();
                break;
            case TURNOUT:
                content = generateTurnoutReport();
                break;
            case CONSTITUENCY:
                content = generateConstituencyReport();
                break;
            default:
                content = "Unknown report type";
        }

        Report report = new Report();
        report.setReportId(UUID.randomUUID().toString());
        report.setGeneratedByAdminId(adminId);
        report.setGeneratedAt(new Date());
        report.setContent(content);
        report.setReportType(type);

        return reportRepository.save(report);
    }

    @Override
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    private String generateResultsReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ELECTION RESULTS REPORT ===\n");
        sb.append("Generated: ").append(new Date()).append("\n\n");

        List<Constituency> constituencies = constituencyRepository.findAll();
        for (Constituency c : constituencies) {
            sb.append("Constituency: ").append(c.getName()).append(" (").append(c.getState()).append(")\n");
            List<Vote> votes = voteRepository.findByConstituencyId(c.getConstituencyId());

            if (votes.isEmpty()) {
                sb.append("  No votes recorded.\n\n");
                continue;
            }

            Map<String, Long> voteCounts = votes.stream()
                    .collect(Collectors.groupingBy(Vote::getCandidateId, Collectors.counting()));

            for (Map.Entry<String, Long> entry : voteCounts.entrySet()) {
                String candidateName = candidateRepository.findById(entry.getKey())
                        .map(Candidate::getName).orElse("Unknown");
                sb.append("  ").append(candidateName).append(": ").append(entry.getValue()).append(" votes\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String generateTurnoutReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== VOTER TURNOUT REPORT ===\n");
        sb.append("Generated: ").append(new Date()).append("\n\n");

        List<Constituency> constituencies = constituencyRepository.findAll();
        long totalRegistered = voterRepository.count();
        long totalVoted = voterRepository.findAll().stream().filter(Voter::isHasVoted).count();

        sb.append("Overall Turnout: ").append(totalVoted).append("/").append(totalRegistered);
        if (totalRegistered > 0) {
            sb.append(" (").append(String.format("%.1f", (totalVoted * 100.0 / totalRegistered))).append("%)");
        }
        sb.append("\n\n");

        for (Constituency c : constituencies) {
            long constituencyVotes = voteRepository.countByConstituencyId(c.getConstituencyId());
            sb.append("Constituency: ").append(c.getName()).append("\n");
            sb.append("  Total Voters: ").append(c.getTotalVoters()).append("\n");
            sb.append("  Votes Cast: ").append(constituencyVotes).append("\n\n");
        }
        return sb.toString();
    }

    private String generateConstituencyReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== CONSTITUENCY REPORT ===\n");
        sb.append("Generated: ").append(new Date()).append("\n\n");

        List<Constituency> constituencies = constituencyRepository.findAll();
        for (Constituency c : constituencies) {
            sb.append("Constituency: ").append(c.getName()).append("\n");
            sb.append("  State: ").append(c.getState()).append("\n");
            sb.append("  Total Registered Voters: ").append(c.getTotalVoters()).append("\n");

            List<Candidate> candidates = candidateRepository.findByConstituencyAndNominationStatus(
                    c, NominationStatus.APPROVED);
            sb.append("  Approved Candidates: ").append(candidates.size()).append("\n");
            for (Candidate cand : candidates) {
                sb.append("    - ").append(cand.getName()).append(" (").append(cand.getPartyName()).append(")\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
