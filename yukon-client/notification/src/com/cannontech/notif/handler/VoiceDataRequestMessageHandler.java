package com.cannontech.notif.handler;

import java.io.IOException;
import java.io.StringWriter;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.cannontech.message.server.ServerRequestMsg;
import com.cannontech.message.util.Message;
import com.cannontech.notif.message.VoiceDataRequestMsg;
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
        VoiceDataRequestMsg msg = (VoiceDataRequestMsg) req.getPayload();
        
        Object callData = _voiceHandler.getCallData(msg.token);
        if (callData instanceof Document) {
            Document xmlDoc = (Document) callData;
            try {
                //TODO if brent wants an XML fragment, this could just process the root node
                
                XMLOutputter out = new XMLOutputter(Format.getCompactFormat());
                StringWriter stringWriter = new StringWriter();
                out.output(xmlDoc, stringWriter);
                
                // send good response
                
                return;
            } catch (IOException e) {
                // log error
            }
        }
        // send bad response
        
        return;
    }

}
