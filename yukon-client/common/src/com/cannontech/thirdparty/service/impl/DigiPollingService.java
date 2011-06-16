package com.cannontech.thirdparty.service.impl;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.events.loggers.ZigbeeEventLogService;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.service.ZigbeeWebService;

public class DigiPollingService {
    private static final Logger logger = YukonLogManager.getLogger(DigiPollingService.class);

    private ZigbeeWebService zigbeeWebService;
    private GatewayDeviceDao gatewayDeviceDao;
    private ScheduledExecutorService globalScheduledExecutor;
    private ConfigurationSource configurationSource;
    private ZigbeeEventLogService zigbeeEventLogService;
    
    /**
     * Gets and processes all files on each gateway from iDigi.
     */
    private Runnable digiDeviceNotificationPoll = new Runnable() {
        public void run() {
            logger.debug("Digi Device Notification Started");
            zigbeeEventLogService.zigbeePollAllGatewaysAttempted();
            
            try {
                //Get all gateways to poll
                List<ZigbeeDevice> gateways = gatewayDeviceDao.getAllGateways();
            
                for (ZigbeeDevice gateway : gateways) {
                    //Poll the gateway.
                    try {

                        zigbeeWebService.processAllDeviceNotificationsOnGateway(gateway);
                    } catch (SocketTimeoutException e) {
                        logger.error("TimeOut while requesting files from gateway", e);
                    }
                }
            } catch (Exception e) {
                logger.error("Exception while Polling Device Notifications", e);
            }
            
            logger.debug("Digi Device Notification Finished");
        }
    };
    
    /**
     * Queries and updates the status of each Device on iDigi account.
     * 
     */
    private Runnable digiDeviceStatusPoll = new Runnable() {

        @Override
        public void run() {
            logger.debug("Digi Device Status Poll Started");
            zigbeeEventLogService.zigbeeRefreshAllGatewaysAttempted();
            
            try {

                zigbeeWebService.refreshAllGateways();
            } catch (Exception e) {
                logger.error("Exception in Digi Device Status Poll", e);
            }
            
            logger.debug("Digi Device Status Poll Finished");
        }
        
    };
    
    @PostConstruct
    public void initialize() {
        boolean runDigi = configurationSource.getString("DIGI_WEBSERVICE_URL") == null?false:true;
        
        if (runDigi) {
            Duration duration = configurationSource.getDuration("DIGI_TIME_BETWEEN_READS", Duration.standardSeconds(600));
            globalScheduledExecutor = Executors.newScheduledThreadPool(2);
            
            globalScheduledExecutor.scheduleWithFixedDelay(digiDeviceNotificationPoll, 5, duration.getStandardSeconds(), TimeUnit.SECONDS);
            logger.info("Digi Device Notification polling has been started.");
            
            duration = configurationSource.getDuration("DIGI_TIME_REFRESH_STATUS", Duration.standardHours(24));
            globalScheduledExecutor.scheduleWithFixedDelay(digiDeviceStatusPoll, 5, duration.getStandardSeconds(), TimeUnit.SECONDS);
            
        } else {
            logger.info("Digi Services not started. No URL configured in master.cfg");
        }
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
    
    @Autowired
    public void setZigbeeEventLogService(ZigbeeEventLogService zigbeeEventLogService) {
        this.zigbeeEventLogService = zigbeeEventLogService;
    }
}
