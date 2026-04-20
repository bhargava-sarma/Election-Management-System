package com.election.controller;

import com.election.dto.LoginDTO;
import com.election.dto.VoterDTO;
import com.election.model.Candidate;
import com.election.model.Voter;
import com.election.model.enums.NominationStatus;
import com.election.model.enums.Role;
import com.election.repository.CandidateRepository;
import com.election.repository.ConstituencyRepository;
import com.election.repository.VoterRepository;
import com.election.service.ElectionService;
import com.election.service.VoterService;
import com.election.util.PasswordUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/voter")
public class VoterController {

    private final VoterService voterService;
    private final VoterRepository voterRepository;
    private final ConstituencyRepository constituencyRepository;
    private final CandidateRepository candidateRepository;
    private final ElectionService electionService;

    public VoterController(VoterService voterService,
                           VoterRepository voterRepository,
                           ConstituencyRepository constituencyRepository,
                           CandidateRepository candidateRepository,
                           ElectionService electionService) {
        this.voterService = voterService;
        this.voterRepository = voterRepository;
        this.constituencyRepository = constituencyRepository;
        this.candidateRepository = candidateRepository;
        this.electionService = electionService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("voterDTO", new VoterDTO());
        model.addAttribute("constituencies", constituencyRepository.findAll());
        return "voter/register";
    }

    @PostMapping("/register")
    public String registerVoter(@Valid @ModelAttribute VoterDTO voterDTO,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("constituencies", constituencyRepository.findAll());
            return "voter/register";
        }
        try {
            voterService.registerVoter(voterDTO);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/voter/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("constituencies", constituencyRepository.findAll());
            return "voter/register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "voter/login";
    }

    @PostMapping("/login")
    public String loginVoter(@Valid @ModelAttribute LoginDTO loginDTO,
                             BindingResult result,
                             HttpSession session,
                             Model model) {
        if (result.hasErrors()) {
            return "voter/login";
        }
        try {
            Voter voter = voterService.login(loginDTO.getEmail(), loginDTO.getPassword());
            session.setAttribute("userId", voter.getUserId());
            session.setAttribute("userName", voter.getName());
            session.setAttribute("userRole", Role.VOTER.name());
            session.setAttribute("loggedInUser", true);
            session.setAttribute("constituencyId", voter.getConstituency().getConstituencyId());
            return "redirect:/voter/dashboard";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "voter/login";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null || !"VOTER".equals(session.getAttribute("userRole"))) {
            return "redirect:/voter/login";
        }
        Voter voter = voterRepository.findById(userId).orElse(null);
        if (voter == null) return "redirect:/voter/login";

        model.addAttribute("voter", voter);
        model.addAttribute("election", electionService.getActiveElection());
        model.addAttribute("votingOpen", electionService.isVotingOpen());
        return "voter/dashboard";
    }

    @GetMapping("/vote")
    public String showBallot(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null || !"VOTER".equals(session.getAttribute("userRole"))) {
            return "redirect:/voter/login";
        }

        Voter voter = voterRepository.findById(userId).orElse(null);
        if (voter == null) return "redirect:/voter/login";

        if (voter.isHasVoted()) {
            return "redirect:/voter/status";
        }

        if (!electionService.isVotingOpen()) {
            model.addAttribute("error", "Voting is not currently open");
            return "voter/dashboard";
        }

        List<Candidate> candidates = candidateRepository.findByConstituencyAndNominationStatus(
                voter.getConstituency(), NominationStatus.APPROVED);

        model.addAttribute("voter", voter);
        model.addAttribute("candidates", candidates);
        model.addAttribute("election", electionService.getActiveElection());
        return "voter/ballot";
    }

    @PostMapping("/vote")
    public String submitVote(@RequestParam String candidateId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null || !"VOTER".equals(session.getAttribute("userRole"))) {
            return "redirect:/voter/login";
        }
        try {
            voterService.castVote(userId, candidateId);
            redirectAttributes.addFlashAttribute("success", "Your vote has been recorded successfully!");
            return "redirect:/voter/status";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/voter/vote";
        }
    }

    @GetMapping("/status")
    public String showStatus(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null || !"VOTER".equals(session.getAttribute("userRole"))) {
            return "redirect:/voter/login";
        }
        Voter voter = voterRepository.findById(userId).orElse(null);
        if (voter == null) return "redirect:/voter/login";

        model.addAttribute("voter", voter);
        model.addAttribute("status", voterService.getVoterStatus(userId));
        return "voter/status";
    }
}
