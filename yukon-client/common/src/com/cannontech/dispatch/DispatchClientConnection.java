package com.cannontech.dispatch;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.CommandMessage;

public class DispatchClientConnection extends com.cannontech.messaging.util.ClientConnection {
    public DispatchClientConnection() {
        super("Dispatch");
    }

    protected void fireMessageEvent(BaseMessage msg) {
        if (msg instanceof CommandMessage && ((CommandMessage) msg).getOperation() == CommandMessage.ARE_YOU_THERE) {
            // Only instances of com.cannontech.message.dispatch.message.Command
            // should
            // get here and it should have a ARE_YOU_THERE operation
            // echo it back so vangogh doesn't time out on us

            write(msg);
        }

        super.fireMessageEvent(msg);
    }
}
