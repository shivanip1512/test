package com.cannontech.thirdparty.digi.service.impl;

import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListSet;
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

public class DigiSmartUpdateServiceImpl implements ZigbeeUpdateService {

    private static final Logger log = YukonLogManager.getLogger(DigiSmartUpdateServiceImpl.class);
    
    private ZigbeeWebService zigbeeWebService;
    private AttributeDynamicDataSource attributeDynamicDataSource;
    
    private boolean smartPolling;
    
    private class SmartRunnable implements Runnable {

        public Duration timeToPoll; 

        private class DeviceToPoll implements Comparable<DeviceToPoll> {
            private ZigbeeDevice device;
            private Instant pollStopTime;
            
            public DeviceToPoll(ZigbeeDevice device,Instant pollStopTime) {
                this.device = device;
                this.pollStopTime = pollStopTime;
            }
            
            public ZigbeeDevice getDevice() {
                return device;
            }
            public Instant getPollStopTime() {
                return pollStopTime;
            }

            @Override
            public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result + getOuterType().hashCode();
                result = prime * result + ((device == null) ? 0 : device.hashCode());
                result = prime * result + ((pollStopTime == null) ? 0 : pollStopTime.hashCode());
                return result;
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj)
                    return true;
                if (obj == null)
                    return false;
                if (getClass() != obj.getClass())
                    return false;
                DeviceToPoll other = (DeviceToPoll) obj;
                if (!getOuterType().equals(other.getOuterType()))
                    return false;
                if (device == null) {
                    if (other.device != null)
                        return false;
                } else if (!device.equals(other.device))
                    return false;
                if (pollStopTime == null) {
                    if (other.pollStopTime != null)
                        return false;
                } else if (!pollStopTime.equals(other.pollStopTime))
                    return false;
                return true;
            }

            private SmartRunnable getOuterType() {
                return SmartRunnable.this;
            }

            @Override
            public int compareTo(DeviceToPoll o) {
                if (this.equals(o)) {
                    return 0;
                }
                // Doesn't greater or less then is not important to us, just giving it an order for ConcurrentSkipListSet 
                if (pollStopTime.isAfter(o.getPollStopTime())) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }
        
        private ConcurrentSkipListSet<DeviceToPoll> skipListSet = new ConcurrentSkipListSet<DigiSmartUpdateServiceImpl.SmartRunnable.DeviceToPoll>();
        
        @Override
        public void run() {           
            Instant now = new Instant();
            
            //Clean up
            Iterator<DeviceToPoll> iterator = skipListSet.iterator();
            while (iterator.hasNext()) {
                DeviceToPoll entry = iterator.next();
               
                Instant stopTime = entry.getPollStopTime();
                if (now.isAfter(stopTime)) {
                    log.debug("Scanning time has expired for " + entry.getDevice().getName());
                    iterator.remove();
                    continue;
                }
                
                PointValueHolder pvh = attributeDynamicDataSource.getPointValue(entry.getDevice(), BuiltInAttribute.ZIGBEE_LINK_STATUS);
                if (pvh.getValue() == Commissioned.DECOMMISSIONED.getRawState() ) {
                    log.debug("Device is in " + Commissioned.DECOMMISSIONED.name() + " state, halting ping. " + entry.getDevice().getName());
                    iterator.remove();
                    continue;
                }
                
                try {
                    if (entry.device.getPaoIdentifier().getPaoType() == PaoType.DIGIGATEWAY) {
                        log.debug("Updating Gateway Status with name, " + entry.getDevice().getName() + ". Scanning will continue until: " + stopTime.toDate());
                        zigbeeWebService.updateGatewayStatus(entry.getDevice());
                    } else {
                        log.debug("Updating EndPoint Status with name, " + entry.getDevice().getName() + ". Scanning will continue until: " + stopTime.toDate());
                        zigbeeWebService.updateEndPointStatus(entry.getDevice());
                    }
                } catch(Exception e) {
                    log.error("Error executing smart poll on device: " + entry.getDevice().getName(),e);
                }

            }        
        }
        
        public void enableSmartPollingForDevice(ZigbeeDevice device) {
            PointValueHolder pvh = attributeDynamicDataSource.getPointValue(device, BuiltInAttribute.ZIGBEE_LINK_STATUS);
            if (pvh.getValue() == Commissioned.DECOMMISSIONED.getRawState()) {
                log.debug("Device is in " + Commissioned.DECOMMISSIONED.name() + " state, will not ping device " + device.getName());
                return;
            }
            
            Instant expireTime = new Instant().plus(timeToPoll);
            skipListSet.add(new DeviceToPoll(device,expireTime));
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
