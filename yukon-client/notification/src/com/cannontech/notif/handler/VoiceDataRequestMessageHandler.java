package com.cannontech.notif.handler;

import java.io.StringWriter;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.message.server.ServerRequestMsg;
import com.cannontech.message.util.Message;
import com.cannontech.notif.message.VoiceDataRequestMsg;
import com.cannontech.notif.message.VoiceDataResponseMsg;
import com.cannontech.notif.outputs.VoiceHandler;

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

    public void handleMessage(Message msg_) {
        
        ServerRequestMsg req = (ServerRequestMsg) msg_;
        VoiceDataRequestMsg reqMsg = (VoiceDataRequestMsg) req.getPayload();

        try {
            Document xmlDoc = _voiceHandler.getCallData(reqMsg.token);

            XMLOutputter out = new XMLOutputter(Format.getCompactFormat());
            StringWriter stringWriter = new StringWriter();
            out.output(xmlDoc.getRootElement(), stringWriter);
            
            VoiceDataResponseMsg rspMsg = new VoiceDataResponseMsg();
            rspMsg.xmlData = stringWriter.toString();
            
            req.createResponseMsg(rspMsg);
            //TODO send the rspMsg

            return;
        } catch (Exception e) {
            CTILogger.warn("Unable to return VoiceXML data for call (token=" + reqMsg.token + ")", e);
        }
        // send bad response

    }

}
