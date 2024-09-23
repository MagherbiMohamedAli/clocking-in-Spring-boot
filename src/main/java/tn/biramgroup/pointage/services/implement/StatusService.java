package tn.biramgroup.pointage.services.implement;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import tn.biramgroup.pointage.Repository.RoleRepository;
import tn.biramgroup.pointage.Repository.StatusRepository;
import tn.biramgroup.pointage.Repository.UserRepository;
import tn.biramgroup.pointage.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class StatusService {
    private static final Logger logger = LoggerFactory.getLogger(StatusService.class);

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StatusRepository statusRepository;
    private final UserService userService;

    @Autowired
    public StatusService(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Transactional
    public void updateUserStatus(Long userId, EStatus newStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Status status = statusRepository.findByStatus(newStatus)
                .orElseThrow(() -> new RuntimeException("Status not found"));

        LocalDateTime now = LocalDateTime.now();

        logger.info("Updating status for user {} to {}", userId, newStatus);

        if (user.getStatus() != null) {
            if (user.getStatus().getStatus() == EStatus.ENTREE && newStatus != EStatus.ENTREE) {
                Duration duration = Duration.between(user.getEntryTime(), now);
                long minutesWorked = duration.toMinutes();
                logger.info("Adding {} minutes to total worked time for user {}", minutesWorked, userId);
                user.setTotalMinutesWorked(user.getTotalMinutesWorked() + minutesWorked);
                userService.saveMonthlyWorkRecord(user, minutesWorked);
                user.setEntryTime(null);
            }

            if (newStatus == EStatus.ENTREE) {
                user.setEntryTime(now);
                logger.info("Set entryTime for user {} to {}", userId, now);
            }
        } else if (newStatus == EStatus.ENTREE) {
            user.setEntryTime(now);
            logger.info("Initial entryTime set for user {}: {}", userId, now);
        }

        user.setStatus(status);
        userRepository.save(user);
        logger.info("User status updated successfully for user {}", userId);
    }

    public long getTotalMinutesWorked(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        long totalMinutes = user.getTotalMinutesWorked() != null ? user.getTotalMinutesWorked() : 0L;

        logger.info("Fetching total minutes worked for user {}: {}", userId, totalMinutes);

        if (user.getStatus() != null && user.getStatus().getStatus() == EStatus.ENTREE && user.getEntryTime() != null) {
            long currentSessionMinutes = ChronoUnit.MINUTES.between(user.getEntryTime(), LocalDateTime.now());
            totalMinutes += currentSessionMinutes;
            logger.info("User {} is currently in ENTREE status. Adding {} minutes from current session. New total: {} minutes",
                    userId, currentSessionMinutes, totalMinutes);
        }

        return totalMinutes;
    }

    public Map<Long, Long> getTotalTimeWorkedForAllUsers() {
        Role employeeRole = roleRepository.findByRole(ERole.ROLE_EMPLOYEE)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        List<User> employees = userRepository.findByRoles(Set.of(employeeRole));
        Map<Long, Long> totalTimeMap = new HashMap<>();

        for (User user : employees) {
            long totalMinutes = getTotalMinutesWorked(user.getId());
            totalTimeMap.put(user.getId(), totalMinutes);
        }

        return totalTimeMap;
    }

    public void resetAllUsersToSortie() {
        Status sortieStatus = statusRepository.findByStatus(EStatus.SORTIE)
                .orElseGet(() -> {
                    Status newStatus = new Status();
                    newStatus.setStatus(EStatus.SORTIE);
                    return statusRepository.save(newStatus);
                });
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getStatus() != null && user.getStatus().getStatus() != EStatus.SORTIE) {
                user.setStatus(sortieStatus);
                user.setEntryTime(null);
                userRepository.save(user);
            }
        }
    }
}
