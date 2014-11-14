package com.cannontech.services.rfn.endpoint;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.GatewayEventLogService;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;
import com.cannontech.common.rfn.model.RfnDevice;
import com.google.common.collect.ImmutableList;

public class GatewayDataResponseListener extends ArchiveRequestListenerBase<RfnIdentifyingMessage> {
    
    private static final Logger log = YukonLogManager.getLogger(GatewayDataResponseListener.class);
    
    @Autowired private GatewayEventLogService gatewayEventLogService;
    
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
            //Don't do anything - we will expect to get a GatewayArchiveRequest soon
            log.warn("Received data for a gateway that is not in the database. Creating " + identifier);
            throw new RuntimeException("Creation not attempted for " + identifier);
        }
        
        @Override
        public void processData(RfnDevice rfnDevice, RfnIdentifyingMessage message) {
            try {
                //This publishes the data to a topic, where the web server will receive and cache it
                log.debug("Publishing gateway data on internal topic: " + message);
                outgoingJmsTemplate.convertAndSend(outgoingTopicName, message);
            } catch (Exception e) {
                log.warn("Data processing failed for " + rfnDevice, e);
                log.debug("Gateway data: " + message);
                throw new RuntimeException("Data processing failed for " + rfnDevice, e);
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
