package com.cannontech.services.rfn.endpoint;

import static com.cannontech.common.rfn.service.RfnDeviceCreationService.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Hours;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.impl.NmSyncServiceImpl;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.UpdateServerConfigHelper;
import com.cannontech.common.events.loggers.GatewayEventLogService;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;
import com.cannontech.common.rfn.message.gateway.GatewayDataResponse;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.core.dao.NotFoundException;
import com.google.common.collect.ImmutableList;

public class GatewayDataResponseListener extends ArchiveRequestListenerBase<RfnIdentifyingMessage> {
    
    private static final Logger log = YukonLogManager.getLogger(GatewayDataResponseListener.class);
    
    @Autowired private GatewayEventLogService gatewayEventLogService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnDeviceLookupService rfnDeviceLookupService;
    @Autowired private RfnGatewayDataCache gatewayCache;
    @Autowired private UpdateServerConfigHelper updateServerConfigHelper;
    @Autowired private NmSyncServiceImpl nmSyncService;
    @Resource(name = "missingGatewayFirstDataTimes") private Map<RfnIdentifier, Instant> missingGatewayFirstDataTimes;
    private JmsTemplate outgoingJmsTemplate;
    private final String outgoingTopicName = "yukon.qr.obj.common.rfn.GatewayDataTopic";
    
    private List<Worker> workers;
    
    private class Worker extends ConverterBase {
        
        public Worker(int workerNumber, int queueSize) {
            super("GatewayDataArchive", workerNumber, queueSize);
        }
        
        @Override
        protected RfnDevice processCreation(RfnIdentifyingMessage message, RfnIdentifier identifier) {
            //We got data for a gateway that is not in the database.
            
            //Look for a device that is identical, except for the model
            //This may be a gateway 2.0 that got put into the system as a gateway 1. Update the type & model.
            if (identifier.getSensorModel().equals(GATEWAY_2_MODEL_STRING)) {
                RfnIdentifier model1Identifier = new RfnIdentifier(identifier.getSensorSerialNumber(),
                                                                   identifier.getSensorManufacturer(),
                                                                   GATEWAY_1_MODEL_STRING);
                
                if (rfnDeviceDao.deviceExists(model1Identifier)) {
                    //Found a match. Update the gateway model to 2.0
                    RfnDevice device = rfnDeviceDao.getDeviceForExactIdentifier(model1Identifier);
                    return rfnDeviceDao.updateGatewayType(device);
                }
            }
            
            // Check to see if we've been receiving data messages for >= 2 hours. If so, we must have missed the
            // creation message. Create the device.
            if (missingGatewayFirstDataTimes.containsKey(identifier)) {
                if (Hours.hoursBetween(missingGatewayFirstDataTimes.get(identifier), Instant.now()).getHours() >= 2) {
                    log.info("Received gateway data for unknown gateway for more than 2 hours. Creating for " + identifier);
                    missingGatewayFirstDataTimes.remove(identifier);
                    try {
                        RfnDevice device = rfnDeviceCreationService.createGateway(identifier.getSensorSerialNumber(), identifier);
                        rfnDeviceCreationService.incrementNewDeviceCreated();
                        log.debug("Created new gateway: " + device);
                        gatewayEventLogService.createdGatewayAutomatically(device.getName(), 
                                                                           device.getRfnIdentifier().getSensorSerialNumber());
                        return device;
                    } catch (Exception e) {
                        log.warn("Waiting to create gateway for " + identifier, e);
                        throw new RuntimeException("Waiting to create gateway for " + identifier, e);
                    }
                } else {
                    log.info("Received data for unknown gateway (" + identifier
                             + "). Gateway will be automatically created if data messages are received over at least"
                             + " 2 hours and no creation message is received for this gateway.");
                    throw new RuntimeException("Waiting to create gateway for " + identifier);
                }
            } else {
                // Received first data message for a gateway that doesn't exist in Yukon. Track it so we can create the
                // gateway if we don't receive a creation request in the next 2 hours.
                missingGatewayFirstDataTimes.put(identifier, Instant.now());
                log.info("Received data for unknown gateway (" + identifier
                         + "). Gateway will be automatically created if data messages are received over at least"
                         + " 2 hours and no creation message is received for this gateway.");
                throw new RuntimeException("Waiting to create gateway for " + identifier);
            }
        }
        
        @Override
        public Optional<String> processData(RfnDevice rfnDevice, RfnIdentifyingMessage message) {
            try {
                //This publishes the data to a topic, where the web server will receive and cache it
                log.debug("Publishing gateway data on internal topic: " + message);
                outgoingJmsTemplate.convertAndSend(outgoingTopicName, message);
                
                //Update service manager cache
                log.debug("Updating gateway data in service manager cache.");
                if (message instanceof GatewayDataResponse) {
                    GatewayDataResponse gatewayDataMessage = (GatewayDataResponse) message;
                    handleDataMessage(gatewayDataMessage);
                    if (StringUtils.isEmpty(gatewayDataMessage.getUpdateServerUrl())) {
                        updateServerConfigHelper.sendNMConfiguration(rfnDevice.getPaoIdentifier().getPaoId());
                    }
                }
                return Optional.empty();  //  no point data to track
            } catch (Exception e) {
                log.warn("Data processing failed for " + rfnDevice, e);
                log.debug("Gateway data: " + message);
                throw new RuntimeException("Data processing failed for " + rfnDevice, e);
            }
        }
    
        private void handleDataMessage(GatewayDataResponse message) {
            RfnIdentifier rfnIdentifier = message.getRfnIdentifier();
            try {
                RfnDevice rfnDevice = rfnDeviceLookupService.getDevice(rfnIdentifier);
                nmSyncService.syncGatewayName(rfnDevice, message.getName());
                log.debug("Handling gateway data message: " + message);
                RfnGatewayData data = new RfnGatewayData(message, rfnDevice.getName());
                gatewayCache.put(rfnDevice.getPaoIdentifier(), data);
            } catch (NotFoundException e) {
                log.error("Unable to add gateway data to cache. Device lookup failed for " + rfnIdentifier);
            }
        }
    }
    
    @Override
    @PostConstruct
    public void init() {
        // setup as many workers as requested
        ImmutableList.Builder<Worker> workerBuilder = ImmutableList.builder();
        int workerCount = getWorkerCount();
        int queueSize = getQueueSize();
        for (int i = 0; i < workerCount; ++i) {
            Worker worker = new Worker(i, queueSize);
            workerBuilder.add(worker);
            worker.start();
        }
        workers = workerBuilder.build();
    }
    
    @PreDestroy
    @Override
    protected void shutdown() {
        for (Worker worker : workers) {
            worker.shutdown();
        }
    }
    
    @Override
    protected List<Worker> getConverters() {
        return workers;
    }
    
    //Not needed, no response is sent for this message
    @Override
    protected Object getRfnArchiveResponse(RfnIdentifyingMessage archiveRequest) {
        return null;
    }
    
    //Not needed, no response is sent for this message
    @Override
    protected String getRfnArchiveResponseQueueName() {
        return null;
    }
    
    @Override
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        super.setConnectionFactory(connectionFactory);
        outgoingJmsTemplate = new JmsTemplate(connectionFactory);
        outgoingJmsTemplate.setPubSubDomain(true);
    }
}
