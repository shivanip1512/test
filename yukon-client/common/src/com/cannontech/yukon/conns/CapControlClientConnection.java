package com.cannontech.yukon.conns;

/**
 * ClientConnection adds functionality necessary to handles connections with CBC
 */
import java.util.HashSet;

import com.cannontech.common.util.MessageEvent;
import com.cannontech.common.util.MessageEventListener;
import com.cannontech.messaging.message.capcontrol.CommandMessage;
import com.cannontech.messaging.message.capcontrol.CommandType;
import com.cannontech.messaging.util.ClientConnection;
import com.google.common.collect.Sets;

public class CapControlClientConnection extends ClientConnection {

    private HashSet<MessageEventListener> messageEventListeners = Sets.newHashSet();

    public CapControlClientConnection() {
        super("CBC");
        setRegistrationMsg(new CommandMessage(CommandType.REQUEST_ALL_DATA.getCommandId()));
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

    public void sendCommand(CommandMessage command) {
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
