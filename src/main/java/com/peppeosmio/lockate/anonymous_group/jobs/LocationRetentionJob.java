package com.peppeosmio.lockate.anonymous_group.jobs;

import com.peppeosmio.lockate.anonymous_group.repository.AGMemberLocationRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Profile("jobs")
@Slf4j
@Component
public class LocationRetentionJob {

    private final AGMemberLocationRepository repository;

    public LocationRetentionJob(AGMemberLocationRepository repository) {
        this.repository = repository;
        log.info("[RetentionJob] Starting retention job");
    }

    // Run every day at 02:00 AM
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void cleanupOldLocations() {
        var before = System.nanoTime();
        var deleted = repository.deleteOldLocations();
        var after = System.nanoTime();
        var durationMs = (after - before) / 1_000_000;
        log.info("[RetentionJob] Deleted {} old location records in {} ms", deleted, durationMs);
    }
}
