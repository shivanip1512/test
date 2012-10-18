package com.cannontech.cbc.cyme.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.StreamMessage;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.cbc.cyme.CymeSimulatorService;
import com.cannontech.cbc.cyme.model.CapControlOperation;
import com.cannontech.cbc.cyme.model.CapControlOperationMessage;
import com.cannontech.clientutils.YukonLogManager;

public class CymeIvvcMessageListener{
    private static final Logger log = YukonLogManager.getLogger(CymeIvvcMessageListener.class);

    @Autowired private CymeSimulatorService cymeSimulatorService;
    
    public void handleOperationMessage(Message message) {
        log.trace("Received Operation Message");
         CapControlOperationMessage operationMessage = null;
                
        if (message instanceof StreamMessage) {
            try {
                operationMessage = buildCapControlOperationMessage((StreamMessage)message);
            } catch (JMSException e) {
                log.error("Error building CapControlOperationMessage from stream.");
                return;
            }
        } else {
            return;
        }
        
        CapControlOperation operation = operationMessage.getOperation();
        
        switch (operation) {
            case OPENBANK: {
                cymeSimulatorService.handleOpenBank(operationMessage.getDeviceId());
               break; 
            }
            case CLOSEBANK: {
                cymeSimulatorService.handleCloseBank(operationMessage.getDeviceId());
                break;
            }
            case LOWERTAP: {
                cymeSimulatorService.handleTapDown(operationMessage.getDeviceId());
                break;
            }
            case RAISETAP: {
                cymeSimulatorService.handleTapUp(operationMessage.getDeviceId());
                break;
            }
            case SCAN: {
                cymeSimulatorService.handleScanDevice(operationMessage.getDeviceId());
                break;
            } case REFRESHSYSTEM: {
                cymeSimulatorService.handleRefreshSystem(operationMessage.getDeviceId());
                break;
            }
        }
    }
    
    private CapControlOperationMessage buildCapControlOperationMessage(StreamMessage message) throws JMSException {
        
        int deviceId = message.readInt();
        int operationId = message.readInt();
        long timestampSeconds = message.readLong();
        
        CapControlOperationMessage operationMessage = new CapControlOperationMessage();
        
        operationMessage.setDeviceId(deviceId);
        operationMessage.setOperation(CapControlOperation.getByOperationId(operationId));
        operationMessage.setTimestamp(new Instant(timestampSeconds));
        
        return operationMessage;
    }
}
