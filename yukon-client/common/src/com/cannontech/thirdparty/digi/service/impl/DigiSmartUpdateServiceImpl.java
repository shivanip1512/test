package com.cannontech.thirdparty.digi.service.impl;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.pao.PaoType;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.service.ZigbeeUpdateService;
import com.cannontech.thirdparty.service.ZigbeeWebService;
import com.google.common.collect.Maps;

public class DigiSmartUpdateServiceImpl implements ZigbeeUpdateService {

    private static final Logger log = YukonLogManager.getLogger(DigiSmartUpdateServiceImpl.class);
    
    private ZigbeeWebService zigbeeWebService;
    
    private boolean smartPolling;
    
    private class SmartRunnable implements Runnable {

        public Duration timeToPoll;
        private Map<ZigbeeDevice,Instant> devicesToPoll = Maps.newHashMap();
        
        @Override
        public void run() {
            for (ZigbeeDevice device : devicesToPoll.keySet()) {
                try {
                    Instant now = new Instant();
    
                    Instant stopTime = devicesToPoll.get(device);
                    if (now.isAfter(stopTime)) {
                        log.debug("Scanning time has expired for " + device.getName());
                        devicesToPoll.remove(device);
                        continue;
                    }
                    
                    if (device.getPaoIdentifier().getPaoType() == PaoType.DIGIGATEWAY) {
                        log.debug("Updating Gateway Status with name, " + device.getName() + ". Scanning will continue until: " + stopTime.toDate());
                        zigbeeWebService.updateGatewayStatus(device);
                    } else {
                        log.debug("Updating EndPoint Status with name, " + device.getName() + ". Scanning will continue until: " + stopTime.toDate());
                        zigbeeWebService.updateEndPointStatus(device);
                    }
                }
                catch(Exception e) {
                    log.error("Error executing smart poll on device: " + device.getName(),e);
                }
            }
        }
        
        public void enableSmartPollingForDevice(ZigbeeDevice device) {
            Instant expireTime = new Instant().plus(timeToPoll);
            devicesToPoll.put(device,expireTime);
        }
    }
    
    private SmartRunnable smartPollingThread;
    private ConfigurationSource configurationSource;
    private ScheduledExecutorService globalScheduledExecutor;
    
    @PostConstruct
    public void initialize() {
        boolean runDigi = configurationSource.getBoolean("DIGI_ENABLED",false);
        smartPolling = false;
        
        if (runDigi) {
            smartPolling = configurationSource.getBoolean("DIGI_SMARTPOLL_ENABLED", false);
            
            if (smartPolling) {
                smartPollingThread = new SmartRunnable();
                globalScheduledExecutor = Executors.newScheduledThreadPool(1);

                Duration readDelay = configurationSource.getDuration("DIGI_SMARTPOLL_DELAY_TIME", Duration.standardSeconds(30));
                smartPollingThread.timeToPoll = configurationSource.getDuration("DIGI_SMARTPOLL_POLL_TIME", Duration.standardSeconds(600));
                globalScheduledExecutor.scheduleWithFixedDelay(smartPollingThread, 15, readDelay.getStandardSeconds(), TimeUnit.SECONDS);

                log.debug("Started DigiSmartUpdateService.");
            }

            log.info("Digi Device Notification polling has been started.");
            
        } else {
            log.info("DigiSmartUpdateService not started. Digi was not enabled.");
        }
    }
    
    @Override
    public void enableActiveDevicePoll(ZigbeeDevice device) {
        if (smartPolling) {            
            smartPollingThread.enableSmartPollingForDevice(device);
        }
    }

    @Autowired
    public void setZigbeeWebService(ZigbeeWebService zigbeeWebService) {
        this.zigbeeWebService = zigbeeWebService;
    }
    
    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
}
