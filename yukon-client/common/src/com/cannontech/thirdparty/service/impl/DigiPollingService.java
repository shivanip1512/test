package com.cannontech.thirdparty.service.impl;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.DurationFieldType;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.model.ZigbeeGateway;
import com.cannontech.thirdparty.service.ZigbeeWebService;

public class DigiPollingService {
    private static final Logger logger = YukonLogManager.getLogger(DigiPollingService.class);

    private ZigbeeWebService zigbeeWebService;
    private GatewayDeviceDao gatewayDeviceDao;
    private ScheduledExecutorService globalScheduledExecutor;
    private ConfigurationSource configurationSource;
    
    private Runnable digiPoller = new Runnable() {
        public void run() {
            logger.info("Digi Poller Started");
            
            try {
                //Get all gateways to poll
                List<ZigbeeGateway> gateways = gatewayDeviceDao.getAllGateways();
            
                for (ZigbeeGateway gateway : gateways) {
                    //Poll the gateway.
                    zigbeeWebService.processAllDeviceNotificationsOnGateway(gateway);
                }
            } catch (Exception e) {
                logger.error("Exception while Polling Gateways", e);
            }
            
            logger.info("Digi Poller Finished");
        }
    };
    
    @PostConstruct
    public void initialize() {
        Duration duration = configurationSource.getDuration("DIGI_TIME_BETWEEN_READS", Duration.standardSeconds(600), DurationFieldType.seconds());
        globalScheduledExecutor = Executors.newScheduledThreadPool(1);
        
        globalScheduledExecutor.scheduleWithFixedDelay(digiPoller, 5, duration.getStandardSeconds(), TimeUnit.SECONDS);
    }
    
    @Autowired
    public void setZigbeeWebService(ZigbeeWebService zigbeeWebService) {
        this.zigbeeWebService = zigbeeWebService;
    }
    
    @Autowired
    public void setGatewayDeviceDao(GatewayDeviceDao gatewayDeviceDao) {
        this.gatewayDeviceDao = gatewayDeviceDao;
    }
    
    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
}
