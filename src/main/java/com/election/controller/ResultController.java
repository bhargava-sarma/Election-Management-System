package com.election.controller;

import com.election.model.Candidate;
import com.election.model.Result;
import com.election.model.enums.ElectionPhase;
import com.election.repository.CandidateRepository;
import com.election.service.ElectionService;
import com.election.service.ResultService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Public controller for viewing election results.
 * Results are only visible when the election phase is RESULT_DECLARED.
 */
@Controller
public class ResultController {

    private final ResultService resultService;
    private final ElectionService electionService;
    private final CandidateRepository candidateRepository;

    public ResultController(ResultService resultService,
                            ElectionService electionService,
                            CandidateRepository candidateRepository) {
        this.resultService = resultService;
        this.electionService = electionService;
        this.candidateRepository = candidateRepository;
    }

    @GetMapping("/results")
    public String viewResults(Model model) {
        var election = electionService.getActiveElection();
        model.addAttribute("election", election);

        if (election != null &&
                election.getCurrentPhase() == ElectionPhase.RESULT_DECLARED) {
            List<Result> results = resultService.getAllResults();

            // Enrich each result with the winner's name and party for display
            for (Result result : results) {
                if (result.getWinnerCandidateId() != null) {
                    Candidate winner = candidateRepository
                            .findById(result.getWinnerCandidateId()).orElse(null);
                    if (winner != null) {
                        result.setWinnerCandidateId(
                                winner.getName() + " (" + winner.getPartyName() + ")");
                    }
                }
            }

            model.addAttribute("results", results);
            model.addAttribute("resultsAvailable", true);
        } else {
            model.addAttribute("resultsAvailable", false);
        }

        return "results";
    }
}
