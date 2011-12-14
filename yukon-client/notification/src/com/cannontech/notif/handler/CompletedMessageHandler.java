package com.cannontech.notif.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.message.notif.NotifCompletedMsg;
import com.cannontech.message.util.Message;
import com.cannontech.notif.outputs.VoiceHandler;
import com.cannontech.notif.server.NotifServerConnection;

public class CompletedMessageHandler implements MessageHandler<NotifCompletedMsg> {

    private @Autowired VoiceHandler voiceHandler;

    @Override
    public Class<NotifCompletedMsg> getSupportedMessageType() {
        return NotifCompletedMsg.class;
    }

    @Override
    public void handleMessage(NotifServerConnection connection,  Message message) {
        NotifCompletedMsg msg = (NotifCompletedMsg) message;
        
        voiceHandler.callStatus(msg.token, msg.status);
    }
}
