package com.cannontech.notif.handler;

import com.cannontech.message.util.Message;
import com.cannontech.message.notif.NotifCompletedMsg;
import com.cannontech.notif.outputs.VoiceHandler;

public class CompletedMessageHandler extends MessageHandler {

    private final VoiceHandler _voiceHandler;

    public CompletedMessageHandler(VoiceHandler handler) {
        _voiceHandler = handler;
    }

    public boolean canHandle(Message msg) {
        return msg instanceof NotifCompletedMsg;
    }

    public void handleMessage(Message msg_) {
        NotifCompletedMsg msg = (NotifCompletedMsg) msg_;
        
        _voiceHandler.completeCall(msg.token, msg.gotConfirmation);
    }

}
