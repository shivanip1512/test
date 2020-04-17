package com.cannontech.services.systemDataPublisher.scheduler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.services.systemDataPublisher.processor.SystemDataHandler;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;
import com.cannontech.services.systemDataPublisher.yaml.model.SystemDataPublisherFrequency;

/**
 * This class is responsible for managing scheduler for System Data generation.
 */
@Service
public class SystemDataSchedulerManager {

    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;
    @Autowired private SystemDataHandler dataHandler;
    private final List<ScheduledFuture<?>> schedulersFuture = new ArrayList<>();

    /**
     * This method will manage schedulers for generating system data.
     */
    public void manageScheduler(List<CloudDataConfiguration> cloudConfiguration) {
        Map<SystemDataPublisherFrequency, List<CloudDataConfiguration>> schedulerToRun = groupConfigurationsByFrequency(
                cloudConfiguration);
        cleanUpSchedulers();
        runScheduler(schedulerToRun);
    }

    /**
     * Starts scheduler at frequency specified.
     */
    private void runScheduler(Map<SystemDataPublisherFrequency, List<CloudDataConfiguration>> schedulerToCreate) {
        for (Entry<SystemDataPublisherFrequency, List<CloudDataConfiguration>> entry : schedulerToCreate.entrySet()) {
            ScheduledFuture<?> futureSchedule = executor.scheduleAtFixedRate(() -> {
                dataHandler.buildAndPublishSystemData(entry.getValue());
            }, 0, entry.getKey().getHours(), TimeUnit.HOURS);
            schedulersFuture.add(futureSchedule);
        }
    }

    /**
     * Stop already started schedules, this will happen when updates are received.
     */
    private void cleanUpSchedulers() {
        schedulersFuture.stream().forEach(future -> {
            future.cancel(true);
        });
        schedulersFuture.clear();
    }

    /**
     * Group CloudDataConfiguration by frequency values. This will give us the time period to create and run scheduler.
     * So if we have number of field having same frequency, then these fields will be processed collectively
     * by one scheduler.
     */
    private Map<SystemDataPublisherFrequency, List<CloudDataConfiguration>> groupConfigurationsByFrequency(
            List<CloudDataConfiguration> configurations) {
        return configurations.stream()
                .filter(configuration -> configuration.getFrequency() != null)
                .collect(Collectors.groupingBy(configuration -> configuration.getFrequency(),
                        LinkedHashMap::new,
                        Collectors.toCollection(ArrayList::new)));
    }
}
