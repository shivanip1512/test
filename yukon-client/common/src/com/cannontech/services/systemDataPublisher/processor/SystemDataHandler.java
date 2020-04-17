package com.cannontech.services.systemDataPublisher.processor;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.services.systemDataPublisher.scheduler.SystemDataSchedulerManager;
import com.cannontech.services.systemDataPublisher.service.SystemDataPublisherService;
import com.cannontech.services.systemDataPublisher.service.impl.SystemDataProcessorFactory;
import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;
import com.cannontech.services.systemDataPublisher.yaml.model.SystemDataPublisherFrequency;

/**
 * Handler is responsible for handling calling other services to create scheduler, pulling data.
 * Once data is received it responsible for publishing it.
 */
@Service
public class SystemDataHandler {

    @Autowired private SystemDataPublisherService systemDataPublisherService;
    @Autowired private SystemDataProcessorFactory systemDataProcessorFactory;
    @Autowired private SystemDataSchedulerManager schedulerManager;

    private static final Logger log = YukonLogManager.getLogger(SystemDataHandler.class);

    /**
     * Handler method to start handling of Cloud Data Configuration.
     * This method calls other services/method for handling data.
     */
    public void handle(List<CloudDataConfiguration> cloudDataConfiguration) {
        List<CloudDataConfiguration> configurationToSchedule = cloudDataConfiguration.stream()
                .filter(e -> e.getFrequency() != SystemDataPublisherFrequency.ON_STARTUP_ONLY).collect(Collectors.toList());
        schedulerManager.manageScheduler(configurationToSchedule);

        cloudDataConfiguration.removeAll(configurationToSchedule);
        buildAndPublishSystemData(cloudDataConfiguration);
    }

    /**
     * Build SystemData. Once the data is build we will publish the data to message broker one by one.
     */
    public void buildAndPublishSystemData(List<CloudDataConfiguration> cloudDataConifguration) {
        for (CloudDataConfiguration conf : cloudDataConifguration) {
            SystemDataProcessor processor = systemDataProcessorFactory.getProcessor(conf);
            SystemData systemData = processor.buildSystemData(conf);
            if (systemData != null) {
                if (log.isDebugEnabled()) {
                    log.info("Publishing system data to topic " + systemData);
                }
                publishSystemData(systemData);
            } else {
                log.info("Neither processor nor source found for field, not publishing it" + conf.getField());
            }
        }
    }

    /**
     * Publish the System data to topic, the interested service will listen to this topic and further push
     * the data to other system.
     */
    private void publishSystemData(SystemData systemData) {
        systemDataPublisherService.publish(systemData);
    }
}
