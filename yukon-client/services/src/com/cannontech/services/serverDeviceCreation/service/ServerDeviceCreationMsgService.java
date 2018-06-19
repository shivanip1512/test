package com.cannontech.services.serverDeviceCreation.service;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SessionAwareMessageListener;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.message.DeviceCreationDescriptor;
import com.cannontech.message.porter.message.RfnDeviceCreationReply;
import com.cannontech.message.porter.message.RfnDeviceCreationRequest;
import com.cannontech.messaging.serialization.MessageSerializer;
import com.cannontech.messaging.serialization.MessageFactory;

public class ServerDeviceCreationMsgService implements SessionAwareMessageListener<BytesMessage> {
    private Logger log = YukonLogManager.getLogger(ServerDeviceCreationMsgService.class);
    private MessageFactory msgFactory;
    
    @Autowired RfnDeviceCreationService rfnDeviceCreationService;
    @Autowired MessageSerializer<RfnDeviceCreationRequest> rfnCreationRequestDeserializer;
    @Autowired MessageSerializer<RfnDeviceCreationReply> rfnCreationReplySerializer;

    @Override
    public void onMessage(BytesMessage message, Session session) {
        try {
            
            //get bytes
            byte[] msgBytes = new byte[(int)message.getBodyLength()];
            message.readBytes(msgBytes);
            
            log.debug("Received RfnDeviceCreationRequest from yukon-server");
            //deserialize message
            RfnDeviceCreationRequest request = (RfnDeviceCreationRequest) rfnCreationRequestDeserializer.deserialize(msgFactory, msgBytes);
            
            //create new device
            RfnIdentifier rfnId = new RfnIdentifier(request.getRfnIdentifier().getSensorSerialNumber(), request.getRfnIdentifier().getSensorManufacturer(), request.getRfnIdentifier().getSensorModel());
            RfnDevice newDevice = rfnDeviceCreationService.create(rfnId);
            log.debug("Created new RFN device: " + newDevice.toString());
            
            //build reply message
            PaoIdentifier paoId = newDevice.getPaoIdentifier();
            DeviceCreationDescriptor descriptor = new DeviceCreationDescriptor(paoId.getPaoId(), paoId.getPaoType().getPaoCategory().getDbString(), paoId.getPaoType().getDbString());
            RfnDeviceCreationReply reply = new RfnDeviceCreationReply(descriptor, true);
            BytesMessage outBytesMsg = session.createBytesMessage();
            outBytesMsg.writeBytes(rfnCreationReplySerializer.serialize(msgFactory, reply));
            log.debug("Created RfnDeviceCreationReply with paoIdentifier: " + paoId.toString());

            //send the reply to the temp queue
            MessageProducer producer = session.createProducer(message.getJMSReplyTo());
            producer.send(outBytesMsg);
        
        } catch (JMSException e) {
            log.error("Unable to handle RFN device creation message", e);
        } catch (Exception e) {
            RfnDeviceCreationReply reply = new RfnDeviceCreationReply(null, false);
            try {
                BytesMessage outBytesMsg = session.createBytesMessage();
                outBytesMsg.writeBytes(rfnCreationReplySerializer.serialize(msgFactory, reply));
                MessageProducer producer = session.createProducer(message.getJMSReplyTo());
                producer.send(outBytesMsg);
            } catch (Exception ee) {
                log.error("Unable to send RFN device creation reply indicating device creation failure", ee);
            }
        }
    }
}
