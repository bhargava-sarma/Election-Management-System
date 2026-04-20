package com.election.controller;

import com.election.dto.CandidateDTO;
import com.election.model.Candidate;
import com.election.model.enums.Role;
import com.election.repository.CandidateRepository;
import com.election.repository.ConstituencyRepository;
import com.election.service.CandidateService;
import com.election.service.ElectionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/candidate")
public class CandidateController {

    private final CandidateService candidateService;
    private final CandidateRepository candidateRepository;
    private final ConstituencyRepository constituencyRepository;
    private final ElectionService electionService;

    public CandidateController(CandidateService candidateService,
                               CandidateRepository candidateRepository,
                               ConstituencyRepository constituencyRepository,
                               ElectionService electionService) {
        this.candidateService = candidateService;
        this.candidateRepository = candidateRepository;
        this.constituencyRepository = constituencyRepository;
        this.electionService = electionService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("candidateDTO", new CandidateDTO());
        model.addAttribute("constituencies", constituencyRepository.findAll());
        return "candidate/register";
    }

    @PostMapping("/register")
    public String registerCandidate(@Valid @ModelAttribute CandidateDTO candidateDTO,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("constituencies", constituencyRepository.findAll());
            return "candidate/register";
        }
        try {
            candidateService.registerNomination(candidateDTO);
            redirectAttributes.addFlashAttribute("success", "Nomination submitted successfully! Please login.");
            return "redirect:/";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("constituencies", constituencyRepository.findAll());
            return "candidate/register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null || !"CANDIDATE".equals(session.getAttribute("userRole"))) {
            return "redirect:/";
        }
        Candidate candidate = candidateRepository.findById(userId).orElse(null);
        if (candidate == null) return "redirect:/";

        model.addAttribute("candidate", candidate);
        model.addAttribute("election", electionService.getActiveElection());
        return "candidate/dashboard";
    }

    @GetMapping("/constituency")
    public String viewConstituency(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null || !"CANDIDATE".equals(session.getAttribute("userRole"))) {
            return "redirect:/";
        }
        Candidate candidate = candidateRepository.findById(userId).orElse(null);
        if (candidate == null) return "redirect:/";

        String constituencyId = candidate.getConstituency().getConstituencyId();
        model.addAttribute("candidate", candidate);
        model.addAttribute("constituency", candidate.getConstituency());
        model.addAttribute("candidates", candidateService.getConstituencyCandidates(constituencyId));
        return "candidate/dashboard";
    }
}
