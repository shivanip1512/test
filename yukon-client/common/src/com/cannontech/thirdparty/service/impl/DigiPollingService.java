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
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.model.ZigbeeDevice;
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
                logger.error("Exception while Polling Gateways", e);
            }
            
            logger.info("Digi Poller Finished");
        }
    };
    
    @PostConstruct
    public void initialize() {
        boolean runDigi = configurationSource.getString("DIGI_WEBSERVICE_URL ") == null?false:true;
        
        if (runDigi) {
            Duration duration = configurationSource.getDuration("DIGI_TIME_BETWEEN_READS", Duration.standardSeconds(600));
            globalScheduledExecutor = Executors.newScheduledThreadPool(1);
            
            globalScheduledExecutor.scheduleWithFixedDelay(digiPoller, 5, duration.getStandardSeconds(), TimeUnit.SECONDS);
            logger.info("Digi Polling Service has been started.");
        } else {
            logger.info("Digi Polling Service not started. No URL configured in master.cfg");
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
}
