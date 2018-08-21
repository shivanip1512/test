package com.cannontech.amr.rfn.dataStreaming.service.impl;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.amr.rfn.dataStreaming.model.DataStreamingConfigException;
import com.cannontech.amr.rfn.dataStreaming.service.DataStreamingCommunicationService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.ScheduledExecutor;

public class DataStreamingStatisticsCollectionService {
    
    private static final Logger log = YukonLogManager.getLogger(DataStreamingStatisticsCollectionService.class);
    @Autowired private DataStreamingCommunicationService dataStreamingCommunicationService;
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private ConfigurationSource configSource;

    @PostConstruct
    public void init() {
        if (!configSource.getBoolean(MasterConfigBoolean.RF_DATA_STREAMING_ENABLED, false)) {
            log.debug("Not scheduling gateway statistics collection");
            return;
        }
        log.info("Scheduling gateway statistics collection to run every hour.");
        scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                log.info("Starting gateway statistics collection.");
                dataStreamingCommunicationService.getGatewayInfo(rfnGatewayService.getAllGateways(), true);
                log.info("Finished gateway statistics collection.");
            } catch (DataStreamingConfigException e) {
                if (configSource.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE)) {
                    log.info("Data Streaming Simulator is not started, gateway statistics collection is not available.");
                } else {
                    log.error("Error occurred during gateway statistics collection", e);
                }
            } catch (Exception e) {
                log.error("Error occurred during gateway statistics collection", e);
            }
        }, 0, 1, TimeUnit.HOURS);
    }
}
