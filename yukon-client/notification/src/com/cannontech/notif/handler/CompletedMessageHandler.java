package com.cannontech.notif.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.notif.VoiceCompletedMessage;
import com.cannontech.notif.outputs.VoiceHandler;
import com.cannontech.notif.server.NotifServerConnection;

public class CompletedMessageHandler implements MessageHandler<VoiceCompletedMessage> {

    private @Autowired VoiceHandler voiceHandler;

    @Override
    public Class<VoiceCompletedMessage> getSupportedMessageType() {
        return VoiceCompletedMessage.class;
    }

    @Override
    public void handleMessage(NotifServerConnection connection,  BaseMessage message) {
        VoiceCompletedMessage msg = (VoiceCompletedMessage) message;
        
        voiceHandler.callStatus(msg.getCallToken(), msg.getCallStatus());
    }
}
