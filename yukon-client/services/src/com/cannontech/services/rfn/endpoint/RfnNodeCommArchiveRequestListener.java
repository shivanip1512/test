package com.cannontech.services.rfn.endpoint;

import java.util.Set;

import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.message.node.RfnNodeCommArchiveRequest;
import com.cannontech.common.rfn.message.node.RfnNodeCommArchiveResponse;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.services.rfn.RfnArchiveProcessor;
import com.cannontech.services.rfn.RfnArchiveQueueHandler;

@ManagedResource
public class RfnNodeCommArchiveRequestListener implements RfnArchiveProcessor {
    private static final Logger log = YukonLogManager.getLogger(RfnNodeCommArchiveRequestListener.class);
    @Autowired private RfnArchiveQueueHandler queueHandler;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    private JmsTemplate jmsTemplate;
    private Logger rfnCommsLog = YukonLogManager.getRfnLogger();

    @Override
    public void process(Object obj, String processor) {
        // received message from NM on startup, persist gateway to device mapping to database, send
        // acknowledgment to NM
        processRequest((RfnNodeCommArchiveRequest) obj, processor);
    }
    
    /**
     * Handles message from NM, logs the message and put in on a queue.
     */
    public void handleArchiveRequest(RfnNodeCommArchiveRequest request) {
        if(rfnCommsLog.isEnabled(Level.INFO)) {
            rfnCommsLog.log(Level.INFO, "<<< " + request.toString());
        }
        queueHandler.add(this, request);
    }
   
    /**
     * Persists gateway to device mapping.
     */
    private void processRequest(RfnNodeCommArchiveRequest request, String processor) {
       Set<Long> referenceIds = rfnDeviceDao.saveDynamicRfnDeviceData(request.getNodeComms());
       sendAcknowledgement(referenceIds, processor);
    }
    
    /**
     * Sends acknowledgement to NM
     */
    private void sendAcknowledgement(Set<Long> referenceIds, String processor) {
        RfnNodeCommArchiveResponse response = new RfnNodeCommArchiveResponse();
        response.setReferenceIDs(referenceIds);
        log.debug("{} acknowledged ids {}", processor, response.getReferenceIDs());
        jmsTemplate.convertAndSend(JmsApiDirectory.RFN_NODE_COMM_ARCHIVE.getResponseQueue().get().getName(), response);
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }
}