package com.cannontech.notif.server;

import com.cannontech.message.util.Message;
import com.cannontech.notif.handler.MessageHandler;
import com.cannontech.notif.message.NotifCompletedMsg;
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
