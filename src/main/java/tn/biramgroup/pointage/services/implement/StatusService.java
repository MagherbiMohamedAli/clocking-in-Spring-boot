package tn.biramgroup.pointage.services.implement;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import tn.biramgroup.pointage.Repository.StatusRepository;
import tn.biramgroup.pointage.Repository.UserRepository;
import tn.biramgroup.pointage.model.EStatus;
import tn.biramgroup.pointage.model.Status;
import tn.biramgroup.pointage.model.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatusService {
    private static final Logger logger = LoggerFactory.getLogger(StatusService.class);

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

        if (user.getStatus() != null) {
            if (user.getStatus().getStatus() == EStatus.ENTREE && newStatus != EStatus.ENTREE) {
                Duration duration = Duration.between(user.getEntryTime(), now);
                long minutesWorked = duration.toMinutes();
                user.setTotalMinutesWorked(user.getTotalMinutesWorked() + minutesWorked);
                userService.saveMonthlyWorkRecord(user, minutesWorked);
                user.setEntryTime(null);
            }

            if (newStatus == EStatus.ENTREE) {
                if (user.getEntryTime() == null) {
                    user.setEntryTime(now);
                } else {
                    user.setEntryTime(now);
                }
                System.out.println("Entry time set to: " + user.getEntryTime());
            }

        } else if (newStatus == EStatus.ENTREE) {
            user.setEntryTime(now);
        }

        user.setStatus(status);
        userRepository.save(user);
    }

    public long getTotalMinutesWorked(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        long totalMinutes = user.getTotalMinutesWorked() != null ? user.getTotalMinutesWorked() : 0L;

        if (user.getStatus() != null && user.getStatus().getStatus() == EStatus.ENTREE && user.getEntryTime() != null) {
            long currentSessionMinutes = ChronoUnit.MINUTES.between(user.getEntryTime(), LocalDateTime.now());
            totalMinutes += currentSessionMinutes;
            logger.info("User {} is currently in ENTREE status. Adding {} minutes from current session. New total: {} minutes",
                    userId, currentSessionMinutes, totalMinutes);
        }

        return totalMinutes;
    }

    public Map<Long, Long> getTotalTimeWorkedForAllUsers() {
        List<User> users = userRepository.findAll();
        Map<Long, Long> totalTimeMap = new HashMap<>();

        for (User user : users) {
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
