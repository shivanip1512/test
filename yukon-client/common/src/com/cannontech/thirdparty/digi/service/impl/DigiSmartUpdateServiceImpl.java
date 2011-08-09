package com.cannontech.thirdparty.digi.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeDynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.db.point.stategroup.Commissioned;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.service.ZigbeeUpdateService;
import com.cannontech.thirdparty.service.ZigbeeWebService;
import com.google.common.collect.Sets;

public class DigiSmartUpdateServiceImpl implements ZigbeeUpdateService {

    private static final Logger log = YukonLogManager.getLogger(DigiSmartUpdateServiceImpl.class);
    
    private ZigbeeWebService zigbeeWebService;
    private AttributeDynamicDataSource attributeDynamicDataSource;
    
    private boolean smartPolling;
    
    private class SmartRunnable implements Runnable {

        public Duration timeToPoll;
        private Map<ZigbeeDevice,Instant> devicesToPoll = Collections.synchronizedMap(new HashMap<ZigbeeDevice, Instant>());
        
        @Override
        public void run() {
            synchronized (devicesToPoll) {
                
                Instant now = new Instant();
                Set<ZigbeeDevice> devicesToRemove = Sets.newHashSet();
                
                //Clean up
                for (ZigbeeDevice device : devicesToPoll.keySet()) {
                    Instant stopTime = devicesToPoll.get(device);
                    if (now.isAfter(stopTime)) {
                        log.debug("Scanning time has expired for " + device.getName());
                        devicesToRemove.add(device);
                        continue;
                    }
                    
                    PointValueHolder pvh = attributeDynamicDataSource.getPointValue(device, BuiltInAttribute.ZIGBEE_LINK_STATUS);
                    if (pvh.getValue() == Commissioned.DECOMMISSIONED.getRawState() ) {
                        log.debug("Device is in " + Commissioned.getForRawState((int)pvh.getValue()) .name() + " state, halting ping. " + device.getName());
                        devicesToRemove.add(device);
                        continue;
                    }
                }
                
                for (ZigbeeDevice device : devicesToRemove) {
                    log.debug("Halting Scanning of, " + device.getName() + ".");
                    devicesToPoll.remove(device);
                }
                
                //Poll away
                for (ZigbeeDevice device : devicesToPoll.keySet()) {
                    try {
                        Instant stopTime = devicesToPoll.get(device);
                        
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
        }
        
        public void enableSmartPollingForDevice(ZigbeeDevice device) {
            synchronized (devicesToPoll) {
                
                PointValueHolder pvh = attributeDynamicDataSource.getPointValue(device, BuiltInAttribute.ZIGBEE_LINK_STATUS);
                if (pvh.getValue() == Commissioned.DECOMMISSIONED.getRawState()) {
                    log.debug("Device is in " + Commissioned.getForRawState((int)pvh.getValue()) .name() + " state, will not ping device " + device.getName());
                    return;
                }
                
                Instant expireTime = new Instant().plus(timeToPoll);
                devicesToPoll.put(device,expireTime);
            }
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
            } else {
                log.info("DigiSmartUpdateService not kicked off. DIGI_SMARTPOLL_ENABLED was false");
            }
        } else {
            log.info("DigiSmartUpdateService not kicked off. DIGI_ENABLED was false.");
        }
    }
    
    @Override
    public void enableActiveDevicePoll(ZigbeeDevice device) {
        if (smartPolling) {            
            smartPollingThread.enableSmartPollingForDevice(device);
        }
    }

    @Autowired
    public void setAttributeDynamicDataSource(AttributeDynamicDataSource attributeDynamicDataSource) {
        this.attributeDynamicDataSource = attributeDynamicDataSource;
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
