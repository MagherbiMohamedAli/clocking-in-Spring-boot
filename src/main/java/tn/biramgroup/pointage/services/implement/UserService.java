package tn.biramgroup.pointage.services.implement;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tn.biramgroup.pointage.Repository.MonthlyWorkRecordRepository;
import tn.biramgroup.pointage.Repository.RoleRepository;
import tn.biramgroup.pointage.Repository.StatusRepository;
import tn.biramgroup.pointage.Repository.UserRepository;
import tn.biramgroup.pointage.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    @Autowired
    private MonthlyWorkRecordRepository monthlyWorkRecordRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    StatusRepository statusRepository;
    @Autowired
    private EmailService emailService;
    private final StatusService statusService;

    @Autowired
    public UserService(@Lazy StatusService statusService) {
        this.statusService = statusService;
    }
    private LocalDateTime lastResetTime;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public void updateUserStatusAndWorkMode(Long userId, Long statusId, Optional<String> workMode) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Update status
        Status status = statusRepository.findById(statusId).orElseThrow(() -> new EntityNotFoundException("Status not found"));
        user.setStatus(status);

        // Update workMode if present
        if (workMode.isPresent()) {
            user.setWorkMode(EWorkMode.valueOf(workMode.get()));
        }

        userRepository.save(user);
    }



    public User addNewUser(User user) {
        try {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String password = passwordEncoder.encode(user.getPassword());
            user.setPassword(password);

            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                Role employeeRole = roleRepository.findByRole(ERole.ROLE_EMPLOYEE)
                        .orElseThrow(() -> new RuntimeException("Error: Default role not found."));
                user.getRoles().add(employeeRole);
            } else {
                Set<Role> existingRoles = new HashSet<>();
                for (Role role : user.getRoles()) {
                    Role existingRole = roleRepository.findByRole(role.getRole())
                            .orElseThrow(() -> new RuntimeException("Error: Role " + role.getRole() + " not found."));
                    existingRoles.add(existingRole);
                }
                user.setRoles(existingRoles);
            }

          //  String verificationToken = generateVerificationToken();
            //user.setVerificationToken(verificationToken);

            User savedUser = userRepository.save(user);

           // String verificationLink = "http://192.168.1.18:8080/api/user/verify?token=" + URLEncoder.encode(verificationToken, StandardCharsets.UTF_8);
            //emailService.sendEmail(user.getEmail(), "Email Verification",
              //      "<p>Please verify your email by clicking the link below:</p><p><a href=\"" + verificationLink + "\">Verify Email</a></p>");

            return savedUser;
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Email already exists.");
        }
    }

   // public User findByVerificationToken(String token) {
     //   return userRepository.findByVerificationToken(token);
   // }


//    private String generateVerificationToken() {
//        SecureRandom random = new SecureRandom();
//        byte[] tokenBytes = new byte[24];
//        random.nextBytes(tokenBytes);
//        return URLEncoder.encode(Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes), StandardCharsets.UTF_8);
//    }

    @Scheduled(cron = "57 59 23 31 * ?")
    public void updateStatuses() {
        statusService.resetAllUsersToSortie();
        System.out.println("Statuses updated to SORTIE.");
    }


   //test
   // @Scheduled(cron = "1 */3 * * * ?")
   @Scheduled(cron = "58 59 23 L * ?")
    public void saveMonthlyRecord() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        Month month = now.getMonth();

        System.out.println("Starting to save monthly records...");

        List<User> users = userRepository.findAll();
        for (User user : users) {
            Long currentTotalMinutesWorked = user.getTotalMinutesWorked();

            System.out.println("Saving total minutes worked for user: " + user.getId() + " -> " + currentTotalMinutesWorked);

            MonthlyWorkRecord record = new MonthlyWorkRecord(user, currentTotalMinutesWorked, year, month);
            monthlyWorkRecordRepository.save(record);
        }

        System.out.println("Monthly work records saved for " + users.size() + " users.");
    }


   //test
   // @Scheduled(cron = "2 */3 * * * ?") // Runs 2 minutes after every 3-minute interval
   @Scheduled(cron = "59 59 23 L * ?")
    public void resetTotalMinutesWorked() {
        System.out.println("Resetting total minutes worked...");

        List<User> users = userRepository.findAll();
        for (User user : users) {
            user.setTotalMinutesWorked(0L);
            userRepository.save(user);
        }

        System.out.println("Total minutes worked reset for " + users.size() + " users.");
    }

    public List<MonthlyWorkRecord> getMonthlyWorkRecords(int year, Month month) {
        return monthlyWorkRecordRepository.findByYearAndMonth(year, month);
    }
    @Transactional
    public void saveMonthlyWorkRecord(User user, Long totalMinutesWorked) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        Month month = now.getMonth();

        MonthlyWorkRecord record = monthlyWorkRecordRepository
                .findByUserAndYearAndMonth(user, year, month)
                .orElse(new MonthlyWorkRecord(user, 0L, year, month));

        record.setTotalMinutesWorked(record.getTotalMinutesWorked() + totalMinutesWorked);
        monthlyWorkRecordRepository.save(record);
    }

   //test
   // @Scheduled(cron = "0 */3 * * * ?") // Runs at the start of every 3 minutes
   @Scheduled(cron = "57 59 23 L * ?") // Runs at 11:57 PM on the last day of every month
   public void updateStatusToSortie() {
        System.out.println("Updating all users' status to SORTIE...");

        List<User> users = userRepository.findAll();

        Status sortieStatus = statusRepository.findByStatus(EStatus.SORTIE)
                .orElseThrow(() -> new RuntimeException("Error: Status SORTIE not found."));

        for (User user : users) {
            if (user.getStatus().getStatus().equals(EStatus.ENTREE)) {
                LocalDateTime entryTime = user.getEntryTime();
                if (entryTime != null) {
                    LocalDateTime now = LocalDateTime.now();
                    long minutesWorked = java.time.Duration.between(entryTime, now).toMinutes();
                    user.setTotalMinutesWorked(user.getTotalMinutesWorked() + minutesWorked);
                    System.out.println("User " + user.getId() + " worked " + minutesWorked + " minutes.");
                }

                user.setStatus(sortieStatus);
                user.setEntryTime(null);
                userRepository.save(user);
            }
        }

        System.out.println("All users' status updated to SORTIE.");
    }



}

