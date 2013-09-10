package com.cannontech.message.dispatch;

import com.cannontech.message.util.Command;
import com.cannontech.message.util.Message;

public class DispatchClientConnection extends com.cannontech.message.util.ClientConnection {
    public DispatchClientConnection() {
        super("Dispatch");
    }

    protected void fireMessageEvent(Message msg) {
        if (msg instanceof Command && ((Command) msg).getOperation() == Command.ARE_YOU_THERE) {
            // Only instances of com.cannontech.message.dispatch.message.Command
            // should
            // get here and it should have a ARE_YOU_THERE operation
            // echo it back so vangogh doesn't time out on us

            write(msg);
        }

        super.fireMessageEvent(msg);
    }
}
