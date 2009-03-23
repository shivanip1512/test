package com.cannontech.notif.handler;

import java.io.StringWriter;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.message.notif.VoiceDataRequestMsg;
import com.cannontech.message.notif.VoiceDataResponseMsg;
import com.cannontech.message.server.ServerRequestMsg;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.ServerRequestHelper;
import com.cannontech.notif.outputs.Notification;
import com.cannontech.notif.outputs.VoiceHandler;
import com.cannontech.notif.server.NotifServerConnection;
import com.cannontech.notif.voice.Call;

public class VoiceDataRequestMessageHandler extends MessageHandler {
    
    private final VoiceHandler _voiceHandler;

    public VoiceDataRequestMessageHandler(VoiceHandler handler) {
        _voiceHandler = handler;
        
    }

    public boolean handleMessage(NotifServerConnection connection,  Message msg_) {
        if (!ServerRequestHelper.isPayloadInstanceOf(msg_, VoiceDataRequestMsg.class)) {
            return false;
        }
        ServerRequestMsg req = (ServerRequestMsg) msg_;
        VoiceDataRequestMsg reqMsg = (VoiceDataRequestMsg) req.getPayload();
        ServerResponseMsg responseMsg = req.createResponseMsg();
        
        
        try {
            Call call = _voiceHandler.getCall(reqMsg.callToken);
            Notification notif = call.getMessage();
            Document xmlDoc = notif.getDocument();

            XMLOutputter out = new XMLOutputter(Format.getCompactFormat());
            StringWriter stringWriter = new StringWriter();
            out.output(xmlDoc.getRootElement(), stringWriter);
            
            VoiceDataResponseMsg rspPayload = new VoiceDataResponseMsg();
            rspPayload.callToken = reqMsg.callToken;
            rspPayload.xmlData = stringWriter.toString();
            rspPayload.contactId = call.getContactId();
            
            responseMsg.setPayload(rspPayload);
            responseMsg.setStatus(ServerResponseMsg.STATUS_OK);
            connection.write(responseMsg);
            
        } catch (Exception e) {
            CTILogger.warn("Unable to return xml parameters for call (token=" + reqMsg.callToken + ")", e);
            responseMsg.setStatus(ServerResponseMsg.STATUS_ERROR);
        }

        connection.write(responseMsg);
        return true;
    }

}
