package tn.biramgroup.pointage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tn.biramgroup.pointage.services.implement.StatusService;

@Component
public class ScheduledTasks {
    @Autowired
    private StatusService statusService;

    @Scheduled(cron = "0 58 23 * * ?")
    public void resetStatuses() {
        statusService.resetAllUsersToSortie();
    }
}
