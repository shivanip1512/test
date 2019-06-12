package com.cannontech.services.rfn.endpoint;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.device.creation.BadTemplateDeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.rfn.Acknowledgeable;
import com.cannontech.common.rfn.endpoint.IgnoredTemplateException;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.archive.RfnDeviceArchiveRequest;
import com.cannontech.common.rfn.message.archive.RfnDeviceArchiveResponse;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dao.NotFoundException;
import com.google.common.collect.Sets;

@ManagedResource
public class RfnDeviceArchiveRequestListener {
    private static final Logger log = YukonLogManager.getLogger(RfnDeviceArchiveRequestListener.class);
    @Autowired private ConfigurationSource configurationSource;
    @Autowired protected RfnDeviceCreationService rfnDeviceCreationService;
    @Autowired private RfnDeviceLookupService rfnDeviceLookupService;
    protected JmsTemplate jmsTemplate;
    private BlockingQueue<Map.Entry<Long, RfnIdentifier>> queue = new LinkedBlockingQueue<>();
    private Logger rfnCommsLog;
    
    @PostConstruct
    public void initialize() {
        rfnCommsLog = YukonLogManager.getRfnLogger();
        int threadCount = configurationSource.getInteger("RFN_METER_DATA_WORKER_COUNT", 5);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        //create processors (default 5)
        IntStream.range(0, threadCount).forEach(i -> executorService.submit(new Processor(queue, i)));
    }
    
    /**
     * Handle message from NM, log the message and put in on a queue.
     */
    public void handleArchiveRequest(RfnDeviceArchiveRequest request) {
        if(rfnCommsLog.isEnabled(Level.INFO)) {
            rfnCommsLog.log(Level.INFO, ">>> " + request.toString());
        }
        request.getRfnIdentifiers().entrySet().forEach(entry -> {
            try {
                queue.put(entry);
            } catch (InterruptedException e) {
               log.error("Failed to add entry {} to queue", entry, e);
            }
        });
    }
    
    private void processRequest(Map.Entry<Long, RfnIdentifier> entry, String processor) {
        RfnIdentifier rfnIdentifier = entry.getValue();
        if (!rfnIdentifier.is_Empty_()) {
            try {
                lookupAndAcknowledge(entry, processor, rfnIdentifier);
            } catch (NotFoundException e) {
                createAndAcknowedge(entry, processor);
            }
        } else {
            acknowledgeEmptyIdentifier(entry, processor, rfnIdentifier);
        }
    }

    private void acknowledgeEmptyIdentifier(Map.Entry<Long, RfnIdentifier> entry, String processor,
            RfnIdentifier rfnIdentifier) {
        if (log.isInfoEnabled()) {
            log.info("Serial Number {}: Sensor Manufacturer:{} Sensor Model:{}",
                rfnIdentifier.getSensorSerialNumber(), rfnIdentifier.getSensorManufacturer(),
                rfnIdentifier.getSensorModel());
        }
        sendAcknowledgement(entry.getKey(), processor);
    }

    private void lookupAndAcknowledge(Map.Entry<Long, RfnIdentifier> entry, String processor,
            RfnIdentifier rfnIdentifier) {
        rfnDeviceCreationService.incrementDeviceLookupAttempt();
        rfnDeviceLookupService.getDevice(rfnIdentifier);
        sendAcknowledgement(entry.getKey(), processor);
    }

    private void createAndAcknowedge(Map.Entry<Long, RfnIdentifier> entry, String processor) {
        try {
            create(entry.getValue());
        } catch (RuntimeException e) {
            boolean isDev = configurationSource.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE);
            boolean isAcknowledgeable = e.getCause() instanceof Acknowledgeable;
            if (isDev || isAcknowledgeable) {
                log.info("Exception:" + e.getMessage());
            } else {
                throw e;
            }
        }
        sendAcknowledgement(entry.getKey(), processor);
    }
    
    private RfnDevice create(RfnIdentifier identifier) {
        try {
            RfnDevice device = rfnDeviceCreationService.create(identifier);
            rfnDeviceCreationService.incrementNewDeviceCreated();
            if (log.isDebugEnabled()) {
                log.debug("Created new device: " + device);
            }
            return device;
        } catch (IgnoredTemplateException e) {
            throw new RuntimeException("Unable to create device for " + identifier + " because template is ignored", e);
        } catch (BadTemplateDeviceCreationException e) {
            log.warn("Creation failed for " + identifier + ". Manufacturer, Model and Serial Number combination do "
                + "not match any templates.", e);
            throw new RuntimeException("Creation failed for " + identifier, e);
        } catch (DeviceCreationException e) {
            log.warn("Creation failed for " + identifier + ", checking cache for any new entries.");
            //  Try another lookup in case someone else beat us to it
            try {
                return rfnDeviceLookupService.getDevice(identifier);
            } catch (NotFoundException e1) {
                throw new RuntimeException("Creation failed for " + identifier, e);
            }
        } catch (Exception e) {
            if (log.isTraceEnabled()) {
                // Only log full exception when trace is on so lots of failed creations don't kill performance.
                log.warn("Creation failed for "+ identifier, e);
            } else {
                log.warn("Creation failed for " + identifier +":" + e);
            }
            throw new RuntimeException("Creation failed for " + identifier, e);
        }
    }

    protected void sendAcknowledgement(Long referenceId, String processor) {
        RfnDeviceArchiveResponse response = new RfnDeviceArchiveResponse();
        response.setReferenceIds(Sets.newHashSet(referenceId));
        log.debug("Acknowledge {} by processor {}");
        jmsTemplate.convertAndSend(JmsApiDirectory.RFN_DEVICE_ARCHIVE.getResponseQueue().get().getName(), response);
    }
    
    
    private class Processor extends Thread {
        private BlockingQueue<Map.Entry<Long, RfnIdentifier>> queue;
        Processor(BlockingQueue<Map.Entry<Long, RfnIdentifier>> queue, int count) {
            this.queue = queue;
            this.setName(JmsApiDirectory.RFN_DEVICE_ARCHIVE + " #"+ (count + 1));
        }

        @Override
        public void run() {
            while(true) {
                try {
                    Map.Entry<Long, RfnIdentifier> entry = queue.take();
                    log.debug("Processor {} processing entry {} remaining in queue {}", this.getName(), entry, queue.size());
                   // processRequest(entry, this.getName());
                } catch (InterruptedException e) {
                    log.error("Processor {} is interrupted", this.getName(), e);
                }
            }
        }
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }
}