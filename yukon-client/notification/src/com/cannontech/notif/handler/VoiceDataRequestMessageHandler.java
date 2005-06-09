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
import com.cannontech.notif.outputs.VoiceHandler;
import com.cannontech.notif.server.NotifServerConnection;

public class VoiceDataRequestMessageHandler extends MessageHandler {
    
    private final VoiceHandler _voiceHandler;

    public VoiceDataRequestMessageHandler(VoiceHandler handler) {
        _voiceHandler = handler;
        
    }

    public boolean canHandle(Message msg) {
        if (msg instanceof ServerRequestMsg) {
            ServerRequestMsg req = (ServerRequestMsg) msg;
            return req.getPayload() instanceof VoiceDataRequestMsg;
        } else {
            return false;
        }
    }

    public void handleMessage(NotifServerConnection connection,  Message msg_) {
        
        ServerRequestMsg req = (ServerRequestMsg) msg_;
        VoiceDataRequestMsg reqMsg = (VoiceDataRequestMsg) req.getPayload();
        ServerResponseMsg responseMsg = req.createResponseMsg();
        
        try {
            Document xmlDoc = _voiceHandler.getCallData(reqMsg.callToken);

            XMLOutputter out = new XMLOutputter(Format.getCompactFormat());
            StringWriter stringWriter = new StringWriter();
            out.output(xmlDoc.getRootElement(), stringWriter);
            
            VoiceDataResponseMsg rspPayload = new VoiceDataResponseMsg();
            rspPayload.xmlData = stringWriter.toString();
            
            responseMsg.setPayload(rspPayload);
            responseMsg.setStatus(ServerResponseMsg.STATUS_OK);
            connection.write(responseMsg);
            
        } catch (Exception e) {
            CTILogger.warn("Unable to return VoiceXML data for call (token=" + reqMsg.callToken + ")", e);
            responseMsg.setStatus(ServerResponseMsg.STATUS_ERROR);
        }

        connection.write(responseMsg);

    }

}
