package com.cannontech.yukon.conns;

/**
 * ClientConnection adds functionality necessary to handles connections with CBC
 */
import java.util.HashSet;

import com.cannontech.common.util.MessageEvent;
import com.cannontech.common.util.MessageEventListener;
import com.cannontech.message.capcontrol.model.CapControlCommand;
import com.cannontech.message.capcontrol.model.CommandType;
import com.google.common.collect.Sets;

public class CapControlClientConnection extends com.cannontech.message.util.ClientConnection {

    private HashSet<MessageEventListener> messageEventListeners = Sets.newHashSet();

    public CapControlClientConnection() {
        super("CBC");
        setRegistrationMsg(new CapControlCommand(CommandType.REQUEST_ALL_DATA.getCommandId()));
    }

    public void addMessageEventListener(MessageEventListener listener) {
        synchronized (messageEventListeners) {
            messageEventListeners.add(listener);
        }
    }

    public void fireMessageEvent(MessageEvent event) {
        synchronized (messageEventListeners) {
            for (MessageEventListener listener : messageEventListeners) {
                listener.messageEvent(event);
            }
        }
    }

    public void sendCommand(CapControlCommand command) {
        if (!isValid()) {
            return;
        }

        synchronized (this) {
            write(command);
        }

        MessageEvent messageEvent =
            new MessageEvent(this, CommandType.getForId(command.getCommandId()).name() + " was executed.");
        messageEvent.setMessageType(MessageEvent.INFORMATION_MESSAGE);
        fireMessageEvent(messageEvent);
    }
}
