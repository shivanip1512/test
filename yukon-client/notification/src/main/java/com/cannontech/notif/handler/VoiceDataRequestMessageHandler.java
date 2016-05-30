package com.cannontech.notif.handler;

import java.io.StringWriter;

import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.message.notif.VoiceDataRequestMsg;
import com.cannontech.message.notif.VoiceDataResponseMsg;
import com.cannontech.message.server.ServerRequestMsg;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.Message;
import com.cannontech.notif.outputs.Notification;
import com.cannontech.notif.outputs.VoiceHandler;
import com.cannontech.notif.server.NotifServerConnection;
import com.cannontech.notif.voice.Call;

public class VoiceDataRequestMessageHandler implements MessageHandler<VoiceDataRequestMsg> {
    
    private @Autowired VoiceHandler voiceHandler;

    @Override
    public Class<VoiceDataRequestMsg> getSupportedMessageType() {
        return VoiceDataRequestMsg.class;
    }
    
    @Override
    public void handleMessage(NotifServerConnection connection,  Message message) {
        ServerRequestMsg req = (ServerRequestMsg) message;
        VoiceDataRequestMsg reqMsg = (VoiceDataRequestMsg) req.getPayload();
        VoiceDataResponseMsg rspPayload = null;
        int status;
        String messageString = null;
        try {
            Call call = voiceHandler.getCall(reqMsg.callToken);
            Notification notif = call.getMessage();
            Document xmlDoc = notif.getDocument();

            XMLOutputter out = new XMLOutputter(Format.getCompactFormat());
            StringWriter stringWriter = new StringWriter();
            out.output(xmlDoc.getRootElement(), stringWriter);
            
            rspPayload = new VoiceDataResponseMsg();
            rspPayload.callToken = reqMsg.callToken;
            rspPayload.xmlData = stringWriter.toString();
            rspPayload.contactId = call.getContactId();
            
            status = ServerResponseMsg.STATUS_OK;
            messageString = "Voice data send successfully";
            
        } catch (Exception e) {
            CTILogger.warn("Unable to return xml parameters for call (token=" + reqMsg.callToken + ")", e);
            messageString = "Error while sending voice data";
            status = ServerResponseMsg.STATUS_ERROR;
        }
        ServerResponseMsg responseMsg = req.createResponseMsg(status, messageString);
        if (status == ServerResponseMsg.STATUS_OK) {
            responseMsg.setPayload(rspPayload);
        }
        connection.write(responseMsg);
    }

}
