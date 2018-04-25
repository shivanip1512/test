package com.cannontech.amr.rfn.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.amr.rfn.service.NmSyncService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.message.RfnArchiveStartupNotification;
import com.cannontech.common.rfn.message.gateway.GatewayEditRequest;
import com.cannontech.common.rfn.message.gateway.GatewaySaveData;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.yukon.conns.ConnPool;

public class NmSyncServiceImpl implements NmSyncService {
    
 private static final int MINUTES_TO_WAIT_TO_SEND_STARTUP_REQUEST = 5;
    
    private static final Logger log = YukonLogManager.getLogger(NmSyncServiceImpl.class);
    
    @Autowired private ConnPool connPool;
    protected JmsTemplate jmsTemplate;
    
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    
    @Override
    public void sendSyncRequest() {
        RfnArchiveStartupNotification notif = new RfnArchiveStartupNotification();
        jmsTemplate.convertAndSend(JmsApiDirectory.ARCHIVE_STARTUP.getQueue().getName(), notif);
        log.info("Startup notification request has been sent to Network manager");
    }
    
    @Override
    public void scheduleSyncRequest() {
        log.info("Waiting " + MINUTES_TO_WAIT_TO_SEND_STARTUP_REQUEST
            + " minutes to send startup notification request to Network Manager");
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    // wait for dispatch to be available before sending notification to NM
                    DispatchClientConnection dispatchConnection = connPool.getDefDispatchConn();
                    if (!dispatchConnection.isValid()) {
                        log.info("Waiting for dispatch to connect");
                        dispatchConnection.waitForValidConnection();
                    }
                    sendSyncRequest();
                } catch (Exception e) {
                    log.error("Failed to send startup notification to Network Manager", e);
                }
            }
        }, MINUTES_TO_WAIT_TO_SEND_STARTUP_REQUEST, TimeUnit.MINUTES);
    }
    
    @Override
    public void syncGatewayName(RfnDevice rfnDevice, String nmGatewayName) {
        if (nmGatewayName != null && !nmGatewayName.isEmpty() && !rfnDevice.getName().equalsIgnoreCase(nmGatewayName)) {
            log.info("Sending message to NM to update gateway name from " + nmGatewayName + " to " + rfnDevice.getName()
                + " for " + rfnDevice);
            sendGatewayNameToNm(rfnDevice);
        }
    }
    
    @Override
    public void syncGatewayName(RfnDevice rfnDevice) {
        log.info("Sending message to NM to update gateway name " + rfnDevice.getName());
        sendGatewayNameToNm(rfnDevice);
    }
    
    /**
     * Sends gateway name to NM
     */
    private void sendGatewayNameToNm(RfnDevice rfnDevice) {
        GatewayEditRequest request = new GatewayEditRequest();
        GatewaySaveData editData = new GatewaySaveData();
        editData.setName(rfnDevice.getName());
        request.setRfnIdentifier(rfnDevice.getRfnIdentifier());
        request.setData(editData);
        jmsTemplate.convertAndSend(JmsApiDirectory.RF_GATEWAY_EDIT.getQueue().getName(), request);
    }

    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }
}
