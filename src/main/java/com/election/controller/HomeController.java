package com.election.controller;

import com.election.dto.LoginDTO;
import com.election.model.*;
import com.election.model.enums.Role;
import com.election.repository.*;
import com.election.service.ElectionService;
import com.election.util.PasswordUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final VoterRepository voterRepository;
    private final CandidateRepository candidateRepository;
    private final ReturningOfficerRepository officerRepository;
    private final AdminRepository adminRepository;
    private final ElectionService electionService;

    public HomeController(VoterRepository voterRepository,
                          CandidateRepository candidateRepository,
                          ReturningOfficerRepository officerRepository,
                          AdminRepository adminRepository,
                          ElectionService electionService) {
        this.voterRepository = voterRepository;
        this.candidateRepository = candidateRepository;
        this.officerRepository = officerRepository;
        this.adminRepository = adminRepository;
        this.electionService = electionService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        model.addAttribute("election", electionService.getActiveElection());
        return "index";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginDTO loginDTO,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        // Try each user type
        var voter = voterRepository.findByEmail(loginDTO.getEmail());
        if (voter.isPresent() && PasswordUtil.matches(loginDTO.getPassword(), voter.get().getPasswordHash())) {
            setSession(session, voter.get().getUserId(), voter.get().getName(), Role.VOTER,
                    voter.get().getConstituency() != null ? voter.get().getConstituency().getConstituencyId() : null);
            return "redirect:/voter/dashboard";
        }

        var candidate = candidateRepository.findByEmail(loginDTO.getEmail());
        if (candidate.isPresent() && PasswordUtil.matches(loginDTO.getPassword(), candidate.get().getPasswordHash())) {
            setSession(session, candidate.get().getUserId(), candidate.get().getName(), Role.CANDIDATE,
                    candidate.get().getConstituency() != null ? candidate.get().getConstituency().getConstituencyId() : null);
            return "redirect:/candidate/dashboard";
        }

        var officer = officerRepository.findByEmail(loginDTO.getEmail());
        if (officer.isPresent() && PasswordUtil.matches(loginDTO.getPassword(), officer.get().getPasswordHash())) {
            setSession(session, officer.get().getUserId(), officer.get().getName(), Role.RETURNING_OFFICER,
                    officer.get().getAssignedConstituency() != null ? officer.get().getAssignedConstituency().getConstituencyId() : null);
            return "redirect:/officer/dashboard";
        }

        var admin = adminRepository.findByEmail(loginDTO.getEmail());
        if (admin.isPresent() && PasswordUtil.matches(loginDTO.getPassword(), admin.get().getPasswordHash())) {
            setSession(session, admin.get().getUserId(), admin.get().getName(), Role.ADMIN, null);
            return "redirect:/admin/dashboard";
        }

        redirectAttributes.addFlashAttribute("error", "Invalid email or password");
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    private void setSession(HttpSession session, String userId, String userName, Role role, String constituencyId) {
        session.setAttribute("userId", userId);
        session.setAttribute("userName", userName);
        session.setAttribute("userRole", role.name());
        session.setAttribute("loggedInUser", true);
        if (constituencyId != null) {
            session.setAttribute("constituencyId", constituencyId);
        }
    }
}
