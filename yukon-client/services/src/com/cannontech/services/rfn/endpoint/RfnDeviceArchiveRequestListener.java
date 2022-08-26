package com.cannontech.services.rfn.endpoint;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.rfn.Acknowledgeable;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.archive.RfnDeviceArchiveRequest;
import com.cannontech.common.rfn.message.archive.RfnDeviceArchiveResponse;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.common.rfn.service.RfDaCreationService;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.services.rfn.RfnArchiveProcessor;
import com.cannontech.services.rfn.RfnArchiveQueueHandler;
import com.google.common.collect.Sets;

@ManagedResource
public class RfnDeviceArchiveRequestListener implements RfnArchiveProcessor {
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private RfnDeviceCreationService rfnDeviceCreationService;
    @Autowired private RfnDeviceLookupService rfnDeviceLookupService;
    @Autowired private RfDaCreationService rfdaCreationService;
    @Autowired private RfnArchiveQueueHandler queueHandler;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;

    private YukonJmsTemplate jmsTemplate;
    private Logger log = YukonLogManager.getRfnLogger();

    @PostConstruct
    public void init() {
        jmsTemplate = jmsTemplateFactory.createResponseTemplate(JmsApiDirectory.RFN_DEVICE_ARCHIVE);
    }

    @Override
    public void process(Object obj, String processor) {
        processRequest((Map.Entry<Long, RfnIdentifier>)obj, processor); 
    }
    
    /**
     * Handles message from NM, logs the message and put in on a queue.
     */
    public void handleArchiveRequest(RfnDeviceArchiveRequest request) {
        log.info("<<< {}", request.toString());
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
                log.debug("LookupAndAcknowledge failed for entry {} Using processor {}", entry.toString());
                createAndAcknowledge(entry, processor);
            }
        } else {
            sendAcknowledgement(entry, processor);
        }
    }

    /**
     * If attempt to look up device by RfnIdentifier is successful, sends acknowledgement to NM
     */
    private void lookupAndAcknowledge(Map.Entry<Long, RfnIdentifier> entry, String processor) {
        rfnDeviceLookupService.getDevice(entry.getValue());
        sendAcknowledgement(entry, processor);
    }

    /**
     * Attempts to create device if successful sends acknowledgement to NM.
     * If creation failed and the mode is dev or device is marked to always acknowledge sends acknowledgement to NM.
     * Otherwise logs exception and acknowledgement is not sent to NM.
     */
    private void createAndAcknowledge(Map.Entry<Long, RfnIdentifier> entry, String processor) {
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
        if (RfnManufacturerModel.is1200(identifier)) {
            rfdaCreationService.create(identifier);
        } else {
            rfnDeviceCreationService.getOrCreate(identifier);
        }
    }

    /**
     * Sends acknowledgement to NM
     */
    private void sendAcknowledgement(Map.Entry<Long, RfnIdentifier> entry, String processor) {
        RfnDeviceArchiveResponse response = new RfnDeviceArchiveResponse();
        response.setReferenceIds(Sets.newHashSet(entry.getKey()));
        if (entry.getValue().is_Empty_()) {
            log.info("{} acknowledged empty rfnIdentifier {} referenceId {}", processor, entry.getValue(),
                entry.getKey());
        }
        log.info(">>> {}", response.toString());
        jmsTemplate.convertAndSend(response);
    }

}