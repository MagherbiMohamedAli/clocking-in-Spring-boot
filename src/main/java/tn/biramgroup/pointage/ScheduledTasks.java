package tn.biramgroup.pointage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tn.biramgroup.pointage.services.implement.StatusService;

@Component
public class ScheduledTasks {
    private static final Logger logger = LoggerFactory.getLogger(StatusService.class);

    @Autowired
    private StatusService statusService;

    @Scheduled(cron = "0 58 23 * * ?")
    public void resetStatuses() {
        logger.info("Running status reset to SORTIE");
        statusService.resetAllUsersToSortie();
    }
}
