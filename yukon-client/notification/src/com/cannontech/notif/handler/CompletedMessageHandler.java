package com.cannontech.notif.handler;

import com.cannontech.message.notif.NotifCompletedMsg;
import com.cannontech.message.util.Message;
import com.cannontech.notif.outputs.VoiceHandler;
import com.cannontech.notif.server.NotifServerConnection;

public class CompletedMessageHandler extends MessageHandler {

    private final VoiceHandler _voiceHandler;

    public CompletedMessageHandler(VoiceHandler handler) {
        _voiceHandler = handler;
    }

    public boolean handleMessage(NotifServerConnection connection,  Message msg_) {
        if (!(msg_ instanceof NotifCompletedMsg)) {
            return false;
        }
        NotifCompletedMsg msg = (NotifCompletedMsg) msg_;
        
        _voiceHandler.callStatus(msg.token, msg.status);
        
        return true;
    }

}
