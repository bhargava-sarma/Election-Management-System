Build a Spring Boot MVC web application for an Election Management System for India.
The project is a university assignment and must follow a strict MVC structure using
Spring Boot as the backend framework, with Thymeleaf as the view layer and MySQL as
the database.

---

### Project Structure

Create the following Maven project with this exact package structure:

com.election
├── controller/
├── model/
├── repository/
├── service/
├── dto/
└── ElectionApplication.java

---

### Database (MySQL)

Create a MySQL schema called `election_db`.
Generate all tables using Spring Data JPA with `spring.jpa.hibernate.ddl-auto=update`.

---

### Models (JPA Entities)

Create the following entity classes in the `model` package:

1. **User** (abstract base — use @MappedSuperclass)
   - userId (String, UUID, primary key)
   - name (String)
   - email (String, unique)
   - passwordHash (String)
   - role (Enum: VOTER, CANDIDATE, RETURNING_OFFICER, ADMIN)

2. **Voter** (extends User)
   - voterId (String)
   - aadhaarNumber (String, unique)
   - hasVoted (boolean, default false)
   - constituency (ManyToOne → Constituency)

3. **Candidate** (extends User)
   - candidateId (String)
   - partyName (String)
   - symbol (String)
   - nominationStatus (Enum: PENDING, APPROVED, REJECTED)
   - constituency (ManyToOne → Constituency)

4. **ReturningOfficer** (extends User)
   - officerId (String)
   - assignedConstituency (ManyToOne → Constituency)

5. **Admin** (extends User)
   - adminId (String)

6. **Election**
   - electionId (String, UUID, primary key)
   - title (String)
   - currentPhase (Enum: SCHEDULED, NOMINATION, VOTING, COUNTING, RESULT_DECLARED)
   - startDate (Date)
   - endDate (Date)

7. **Constituency**
   - constituencyId (String, UUID, primary key)
   - name (String)
   - state (String)
   - totalVoters (int)

8. **Ballot**
   - ballotId (String, UUID, primary key)
   - election (ManyToOne → Election)
   - constituency (ManyToOne → Constituency)

9. **Vote**
   - voteId (String, UUID, primary key)
   - voterId (String)
   - candidateId (String)
   - constituencyId (String)
   - timestamp (Date)

10. **Result**
    - resultId (String, UUID, primary key)
    - constituency (OneToOne → Constituency)
    - winnerCandidateId (String)
    - voteCounts (store as JSON string or use a separate VoteCount table)

11. **Report**
    - reportId (String, UUID, primary key)
    - generatedByAdminId (String)
    - generatedAt (Date)
    - content (Text)
    - reportType (Enum: RESULTS, TURNOUT, CONSTITUENCY)

---

### Repositories

Create Spring Data JPA repositories for all entities in the `repository` package.
Each should extend JpaRepository.

Example:
- VoterRepository extends JpaRepository<Voter, String>
- CandidateRepository extends JpaRepository<Candidate, String>
- ElectionRepository extends JpaRepository<Election, String>
- VoteRepository extends JpaRepository<Vote, String>
- ConstituencyRepository extends JpaRepository<Constituency, String>
- ResultRepository extends JpaRepository<Result, String>
- ReportRepository extends JpaRepository<Report, String>

---

### Services

Create service classes in the `service` package with interfaces and implementations.

1. **VoterService**
   - registerVoter(VoterDTO dto): void
   - login(String email, String password): Voter
   - castVote(String voterId, String candidateId): Vote
   - getVoterStatus(String voterId): String

2. **CandidateService**
   - registerNomination(CandidateDTO dto): void
   - getConstituencyCandidates(String constituencyId): List<Candidate>
   - approveCandidate(String candidateId): void
   - rejectCandidate(String candidateId): void

3. **ElectionService**
   - createElection(ElectionDTO dto): void
   - changePhase(String electionId, ElectionPhase phase): void
   - getActiveElection(): Election
   - isVotingOpen(): boolean

4. **ReportService**
   - generateReport(String adminId, ReportType type): Report
   - getAllReports(): List<Report>

---

### Controllers (Spring MVC)

Create controllers in the `controller` package.

1. **VoterController** — maps to `/voter`
   - GET  `/voter/register` → show registration form
   - POST `/voter/register` → handle registration
   - GET  `/voter/login`    → show login form
   - POST `/voter/login`    → handle login, redirect to dashboard
   - GET  `/voter/dashboard`→ show voter dashboard
   - GET  `/voter/vote`     → show ballot
   - POST `/voter/vote`     → submit vote
   - GET  `/voter/status`   → show vote status and receipt

2. **CandidateController** — maps to `/candidate`
   - GET  `/candidate/register`   → show nomination form
   - POST `/candidate/register`   → submit nomination
   - GET  `/candidate/dashboard`  → show candidate dashboard
   - GET  `/candidate/constituency` → view constituency details

3. **ReturningOfficerController** — maps to `/officer`
   - GET  `/officer/dashboard`    → list pending nominations
   - POST `/officer/approve/{id}` → approve candidate
   - POST `/officer/reject/{id}`  → reject candidate
   - POST `/officer/phase`        → change election phase

4. **AdminController** — maps to `/admin`
   - GET  `/admin/dashboard`      → admin home
   - GET  `/admin/reports`        → list all reports
   - POST `/admin/reports/generate` → generate new report
   - GET  `/admin/reports/{id}`   → view specific report

---

### Views (Thymeleaf Templates)

Create Thymeleaf HTML templates in `src/main/resources/templates/` with the following structure:

templates/
├── voter/
│   ├── register.html
│   ├── login.html
│   ├── dashboard.html
│   ├── ballot.html
│   └── status.html
├── candidate/
│   ├── register.html
│   └── dashboard.html
├── officer/
│   └── dashboard.html
├── admin/
│   ├── dashboard.html
│   └── reports.html
└── index.html

Each page should have a simple, clean white-background layout with a navbar showing
the current user's role and a logout button. Use basic Bootstrap 5 (CDN) for styling.

---

### application.properties
- spring.datasource.url=jdbc:mysql://localhost:3306/election_db
- spring.datasource.username=root
- spring.datasource.password=root
- spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
- spring.jpa.hibernate.ddl-auto=update
- spring.jpa.show-sql=true
- spring.thymeleaf.cache=false
- server.port=8080

---

### pom.xml Dependencies

Include the following dependencies:
- spring-boot-starter-web
- spring-boot-starter-thymeleaf
- spring-boot-starter-data-jpa
- spring-boot-starter-security (optional, for session management)
- mysql-connector-j
- lombok
- spring-boot-devtools

---

### Additional Notes

- Use constructor injection (not field injection) in all service and controller classes.
- All service classes must have an interface and an implementation class
  (e.g. VoterService interface + VoterServiceImpl class). This is required for
  the Dependency Inversion Principle demonstration in the assignment.
- Add basic form validation using @Valid and BindingResult in controllers.
- Use DTOs (Data Transfer Objects) in the `dto` package to pass data between
  controllers and services — do not expose entity classes directly to the view.
- Add a data seeder class (CommandLineRunner) that inserts one sample Election
  and two sample Constituencies on startup if the database is empty.
- Keep all business logic in the service layer — controllers should only handle
  HTTP requests and delegate to services.
