package com.election.controller;

import com.election.model.Candidate;
import com.election.model.Election;
import com.election.model.Result;
import com.election.model.ReturningOfficer;
import com.election.model.enums.ElectionPhase;
import com.election.model.enums.NominationStatus;
import com.election.repository.CandidateRepository;
import com.election.repository.ReturningOfficerRepository;
import com.election.service.CandidateService;
import com.election.service.ElectionService;
import com.election.service.ResultService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/officer")
public class ReturningOfficerController {

    private final CandidateService candidateService;
    private final CandidateRepository candidateRepository;
    private final ReturningOfficerRepository officerRepository;
    private final ElectionService electionService;
    private final ResultService resultService;

    public ReturningOfficerController(CandidateService candidateService,
                                      CandidateRepository candidateRepository,
                                      ReturningOfficerRepository officerRepository,
                                      ElectionService electionService,
                                      ResultService resultService) {
        this.candidateService = candidateService;
        this.candidateRepository = candidateRepository;
        this.officerRepository = officerRepository;
        this.electionService = electionService;
        this.resultService = resultService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null || !"RETURNING_OFFICER".equals(session.getAttribute("userRole"))) {
            return "redirect:/";
        }

        ReturningOfficer officer = officerRepository.findById(userId).orElse(null);
        if (officer == null) return "redirect:/";

        List<Candidate> pendingCandidates = candidateRepository.findByConstituencyAndNominationStatus(
                officer.getAssignedConstituency(), NominationStatus.PENDING);
        List<Candidate> allCandidates = candidateRepository.findByConstituency(officer.getAssignedConstituency());

        Election election = electionService.getActiveElection();

        model.addAttribute("officer", officer);
        model.addAttribute("pendingCandidates", pendingCandidates);
        model.addAttribute("allCandidates", allCandidates);
        model.addAttribute("election", election);
        model.addAttribute("phases", ElectionPhase.values());

        // Show counting results preview when in COUNTING or RESULT_DECLARED phase
        if (election != null &&
                (election.getCurrentPhase() == ElectionPhase.COUNTING ||
                 election.getCurrentPhase() == ElectionPhase.RESULT_DECLARED)) {
            model.addAttribute("countingResults", resultService.getAllResults());
        }

        return "officer/dashboard";
    }

    @PostMapping("/approve/{id}")
    public String approveCandidate(@PathVariable("id") String id,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (!"RETURNING_OFFICER".equals(session.getAttribute("userRole"))) {
            return "redirect:/";
        }
        try {
            candidateService.approveCandidate(id);
            redirectAttributes.addFlashAttribute("success", "Candidate approved successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/officer/dashboard";
    }

    @PostMapping("/reject/{id}")
    public String rejectCandidate(@PathVariable("id") String id,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (!"RETURNING_OFFICER".equals(session.getAttribute("userRole"))) {
            return "redirect:/";
        }
        try {
            candidateService.rejectCandidate(id);
            redirectAttributes.addFlashAttribute("success", "Candidate rejected");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/officer/dashboard";
    }

    @PostMapping("/phase")
    public String changePhase(@RequestParam String electionId,
                              @RequestParam ElectionPhase phase,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (!"RETURNING_OFFICER".equals(session.getAttribute("userRole"))) {
            return "redirect:/";
        }
        try {
            electionService.changePhase(electionId, phase);
            redirectAttributes.addFlashAttribute("success", "Election phase changed to " + phase);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/officer/dashboard";
    }
}
