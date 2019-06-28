package com.cannontech.services.rfn.endpoint;

import java.util.Map;

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
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.common.rfn.service.RfDaCreationService;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.services.rfn.RfnArchiveProcessor;
import com.cannontech.services.rfn.RfnArchiveQueueHandler;
import com.google.common.collect.Sets;

@ManagedResource
public class RfnDeviceArchiveRequestListener implements RfnArchiveProcessor {
    private static final Logger log = YukonLogManager.getLogger(RfnDeviceArchiveRequestListener.class);
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private RfnDeviceCreationService rfnDeviceCreationService;
    @Autowired private RfnDeviceLookupService rfnDeviceLookupService;
    @Autowired private RfDaCreationService rfdaCreationService;
    @Autowired private RfnArchiveQueueHandler queueHandler;
    private JmsTemplate jmsTemplate;
    private Logger rfnCommsLog = YukonLogManager.getRfnLogger();

    @Override
    public void process(Object obj, String processor) {
        processRequest((Map.Entry<Long, RfnIdentifier>)obj, processor); 
    }
    
    /**
     * Handles message from NM, logs the message and put in on a queue.
     */
    public void handleArchiveRequest(RfnDeviceArchiveRequest request) {
        if(rfnCommsLog.isEnabled(Level.INFO)) {
            rfnCommsLog.log(Level.INFO, "<<< " + request.toString());
        }
        request.getRfnIdentifiers().entrySet().forEach(entry -> queueHandler.add(this, entry));
    }
    
    /**
     * Attempts to lookup or create a device. Sends acknowledgment to NM.
     */
    private void processRequest(Map.Entry<Long, RfnIdentifier> entry, String processor) {
        if (!entry.getValue().is_Empty_()) {
            try {
                lookupAndAcknowledge(entry, processor);
            } catch (Exception e) {
                createAndAcknowedge(entry, processor);
            }
        } else {
            sendAcknowledgement(entry, processor);
        }
    }

    /**
     * If attempt to look up device by RfnIdentifier is successful, sends acknowledgement to NM
     */
    private void lookupAndAcknowledge(Map.Entry<Long, RfnIdentifier> entry, String processor) {
        rfnDeviceCreationService.incrementDeviceLookupAttempt();
        rfnDeviceLookupService.getDevice(entry.getValue());
        sendAcknowledgement(entry, processor);
    }

    /**
     * Attempts to create device if successful sends acknowledgement to NM.
     * If creation failed and the mode is dev or device is marked to always acknowledge sends acknowledgement to NM.
     * Otherwise logs exception and acknowledgement is not sent to NM.
     */
    private void createAndAcknowedge(Map.Entry<Long, RfnIdentifier> entry, String processor) {
        try {
            create(entry.getValue(), processor);
            sendAcknowledgement(entry, processor);
        } catch (RuntimeException e) {
            boolean isDev = configurationSource.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE);
            boolean isAcknowledgeable = e.getCause() instanceof Acknowledgeable;
            if (isDev || isAcknowledgeable) {
                log.info("Exception:" + e.getMessage(), e);
                sendAcknowledgement(entry, processor);
            } else {
                log.warn("{} {} device creation failed {} {}", processor, this.getClass().getSimpleName(),
                    entry.getKey(), entry.getValue());
            }
        }
    }
    
    /**
     * Attempts to create device
     */
    private void create(RfnIdentifier identifier, String processor) {
        try {
            RfnDevice device = null;
            if (RfnManufacturerModel.is1200(identifier)) {
                device = rfdaCreationService.create(identifier);
                rfdaCreationService.incrementNewDeviceCreated(); 
            } else {
                device = rfnDeviceCreationService.create(identifier);
                rfnDeviceCreationService.incrementNewDeviceCreated();
            }
            if (log.isDebugEnabled()) {
                log.debug("{} created device {} ", processor, device);
            }
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
                rfnDeviceLookupService.getDevice(identifier);
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

    /**
     * Sends acknowledgement to NM
     */
    private void sendAcknowledgement(Map.Entry<Long, RfnIdentifier> entry, String processor) {
        RfnDeviceArchiveResponse response = new RfnDeviceArchiveResponse();
        response.setReferenceIds(Sets.newHashSet(entry.getKey()));
        if (entry.getValue().is_Empty_()) {
            log.info("{} acknowledged empty rfnIdentifier {} referenceId={}", processor, entry.getValue(),
                entry.getKey());
        } else {
            log.debug("{} acknowledged referenceId={}", processor, entry.getKey());
        }
        jmsTemplate.convertAndSend(JmsApiDirectory.RFN_DEVICE_ARCHIVE.getResponseQueue().get().getName(), response);
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }
}