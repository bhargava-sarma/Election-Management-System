package com.election.controller;

import com.election.model.Constituency;
import com.election.model.Election;
import com.election.model.Report;
import com.election.model.enums.ElectionPhase;
import com.election.model.enums.ReportType;
import com.election.repository.*;
import com.election.service.ElectionService;
import com.election.service.ReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ReportService reportService;
    private final ElectionService electionService;
    private final VoterRepository voterRepository;
    private final CandidateRepository candidateRepository;
    private final ConstituencyRepository constituencyRepository;
    private final VoteRepository voteRepository;
    private final ResultRepository resultRepository;
    private final ElectionRepository electionRepository;
    private final ReportRepository reportRepository;

    public AdminController(ReportService reportService,
                           ElectionService electionService,
                           VoterRepository voterRepository,
                           CandidateRepository candidateRepository,
                           ConstituencyRepository constituencyRepository,
                           VoteRepository voteRepository,
                           ResultRepository resultRepository,
                           ElectionRepository electionRepository,
                           ReportRepository reportRepository) {
        this.reportService = reportService;
        this.electionService = electionService;
        this.voterRepository = voterRepository;
        this.candidateRepository = candidateRepository;
        this.constituencyRepository = constituencyRepository;
        this.voteRepository = voteRepository;
        this.resultRepository = resultRepository;
        this.electionRepository = electionRepository;
        this.reportRepository = reportRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null || !"ADMIN".equals(session.getAttribute("userRole"))) {
            return "redirect:/";
        }

        model.addAttribute("totalVoters", voterRepository.count());
        model.addAttribute("totalCandidates", candidateRepository.count());
        model.addAttribute("totalConstituencies", constituencyRepository.count());
        model.addAttribute("election", electionService.getActiveElection());
        return "admin/dashboard";
    }

    @GetMapping("/reports")
    public String listReports(HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("userRole"))) {
            return "redirect:/";
        }
        model.addAttribute("reports", reportService.getAllReports());
        model.addAttribute("reportTypes", ReportType.values());
        return "admin/reports";
    }

    @PostMapping("/reports/generate")
    public String generateReport(@RequestParam ReportType reportType,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null || !"ADMIN".equals(session.getAttribute("userRole"))) {
            return "redirect:/";
        }
        try {
            reportService.generateReport(userId, reportType);
            redirectAttributes.addFlashAttribute("success", "Report generated successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/reports";
    }

    @GetMapping("/reports/{id}")
    public String viewReport(@PathVariable("id") String id,
                             HttpSession session,
                             Model model) {
        if (!"ADMIN".equals(session.getAttribute("userRole"))) {
            return "redirect:/";
        }
        Report report = reportService.getAllReports().stream()
                .filter(r -> r.getReportId().equals(id))
                .findFirst().orElse(null);

        if (report == null) {
            return "redirect:/admin/reports";
        }
        model.addAttribute("report", report);
        return "admin/reports";
    }

    @PostMapping("/reset")
    public String resetElectionData(HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null || !"ADMIN".equals(session.getAttribute("userRole"))) {
            return "redirect:/";
        }
        try {
            // Clear all transactional data
            voteRepository.deleteAll();
            resultRepository.deleteAll();
            reportRepository.deleteAll();
            voterRepository.deleteAll();
            candidateRepository.deleteAll();

            // Reset constituency voter counts to 0
            for (Constituency c : constituencyRepository.findAll()) {
                c.setTotalVoters(0);
                constituencyRepository.save(c);
            }

            // Reset election phase back to SCHEDULED
            Election election = electionService.getActiveElection();
            if (election != null) {
                election.setCurrentPhase(ElectionPhase.SCHEDULED);
                electionRepository.save(election);
            }

            redirectAttributes.addFlashAttribute("success",
                    "All voters, candidates, votes, results, and reports have been cleared. Election reset to SCHEDULED.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Reset failed: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}
