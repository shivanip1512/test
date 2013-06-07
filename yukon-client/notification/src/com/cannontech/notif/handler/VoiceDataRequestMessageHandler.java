package com.cannontech.notif.handler;

import java.io.StringWriter;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.notif.VoiceDataRequestMessage;
import com.cannontech.messaging.message.notif.VoiceDataResponseMessage;
import com.cannontech.messaging.message.server.ServerRequestMessage;
import com.cannontech.messaging.message.server.ServerResponseMessage;
import com.cannontech.notif.outputs.Notification;
import com.cannontech.notif.outputs.VoiceHandler;
import com.cannontech.notif.server.NotifServerConnection;
import com.cannontech.notif.voice.Call;

public class VoiceDataRequestMessageHandler implements MessageHandler<VoiceDataRequestMessage> {
    
    private @Autowired VoiceHandler voiceHandler;

    @Override
    public Class<VoiceDataRequestMessage> getSupportedMessageType() {
        return VoiceDataRequestMessage.class;
    }
    
    @Override
    public void handleMessage(NotifServerConnection connection,  BaseMessage message) {
        ServerRequestMessage req = (ServerRequestMessage) message;
        VoiceDataRequestMessage reqMsg = (VoiceDataRequestMessage) req.getPayload();
        ServerResponseMessage responseMsg = req.createResponseMsg();
        
        try {
            Call call = voiceHandler.getCall(reqMsg.getCallToken());
            Notification notif = call.getMessage();
            Document xmlDoc = notif.getDocument();

            XMLOutputter out = new XMLOutputter(Format.getCompactFormat());
            StringWriter stringWriter = new StringWriter();
            out.output(xmlDoc.getRootElement(), stringWriter);
            
            VoiceDataResponseMessage rspPayload = new VoiceDataResponseMessage();
            rspPayload.setCallToken(reqMsg.getCallToken());
            rspPayload.setXmlData(stringWriter.toString());
            rspPayload.setContactId(call.getContactId());
            
            responseMsg.setPayload(rspPayload);
            responseMsg.setStatus(ServerResponseMessage.STATUS_OK);
            connection.write(responseMsg);
            
        } catch (Exception e) {
            CTILogger.warn("Unable to return xml parameters for call (token=" + reqMsg.getCallToken() + ")", e);
            responseMsg.setStatus(ServerResponseMessage.STATUS_ERROR);
        }

        connection.write(responseMsg);
    }

}
