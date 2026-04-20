package com.election.config;

import com.election.model.*;
import com.election.model.enums.ElectionPhase;
import com.election.model.enums.Role;
import com.election.repository.*;
import com.election.util.PasswordUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.UUID;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ElectionRepository electionRepository;
    private final ConstituencyRepository constituencyRepository;
    private final AdminRepository adminRepository;
    private final ReturningOfficerRepository officerRepository;

    public DataSeeder(ElectionRepository electionRepository,
                      ConstituencyRepository constituencyRepository,
                      AdminRepository adminRepository,
                      ReturningOfficerRepository officerRepository) {
        this.electionRepository = electionRepository;
        this.constituencyRepository = constituencyRepository;
        this.adminRepository = adminRepository;
        this.officerRepository = officerRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only seed if database is empty
        if (electionRepository.count() > 0) {
            System.out.println("Database already seeded. Skipping...");
            return;
        }

        System.out.println("Seeding database with sample data...");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Create sample Election
        Election election = new Election();
        election.setElectionId(UUID.randomUUID().toString());
        election.setTitle("General Election 2026");
        election.setCurrentPhase(ElectionPhase.SCHEDULED);
        election.setStartDate(sdf.parse("2026-05-01"));
        election.setEndDate(sdf.parse("2026-05-31"));
        electionRepository.save(election);

        // Create two sample Constituencies
        Constituency c1 = new Constituency();
        c1.setConstituencyId(UUID.randomUUID().toString());
        c1.setName("Mumbai North");
        c1.setState("Maharashtra");
        c1.setTotalVoters(0);
        constituencyRepository.save(c1);

        Constituency c2 = new Constituency();
        c2.setConstituencyId(UUID.randomUUID().toString());
        c2.setName("New Delhi");
        c2.setState("Delhi");
        c2.setTotalVoters(0);
        constituencyRepository.save(c2);

        // Create a default Admin
        Admin admin = new Admin();
        admin.setUserId(UUID.randomUUID().toString());
        admin.setAdminId("ADM-001");
        admin.setName("System Admin");
        admin.setEmail("admin@election.com");
        admin.setPasswordHash(PasswordUtil.hashPassword("admin123"));
        admin.setRole(Role.ADMIN);
        adminRepository.save(admin);

        // Create Returning Officers for each constituency
        ReturningOfficer officer1 = new ReturningOfficer();
        officer1.setUserId(UUID.randomUUID().toString());
        officer1.setOfficerId("OFF-001");
        officer1.setName("R.O. Mumbai North");
        officer1.setEmail("officer1@election.com");
        officer1.setPasswordHash(PasswordUtil.hashPassword("officer123"));
        officer1.setRole(Role.RETURNING_OFFICER);
        officer1.setAssignedConstituency(c1);
        officerRepository.save(officer1);

        ReturningOfficer officer2 = new ReturningOfficer();
        officer2.setUserId(UUID.randomUUID().toString());
        officer2.setOfficerId("OFF-002");
        officer2.setName("R.O. New Delhi");
        officer2.setEmail("officer2@election.com");
        officer2.setPasswordHash(PasswordUtil.hashPassword("officer123"));
        officer2.setRole(Role.RETURNING_OFFICER);
        officer2.setAssignedConstituency(c2);
        officerRepository.save(officer2);

        System.out.println("Database seeding complete!");
        System.out.println("  Admin login: admin@election.com / admin123");
        System.out.println("  Officer 1 login: officer1@election.com / officer123");
        System.out.println("  Officer 2 login: officer2@election.com / officer123");
    }
}
