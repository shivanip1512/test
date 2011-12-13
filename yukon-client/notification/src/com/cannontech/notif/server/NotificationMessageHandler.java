package com.cannontech.notif.server;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.util.*;
import com.cannontech.notif.handler.MessageHandler;

/**
 * Handles messages for the Notification Server.
 */
public class NotificationMessageHandler implements MessageListener
{
    private static final Logger log = YukonLogManager.getLogger(NotificationMessageHandler.class);
    
    private @Autowired List<MessageHandler> messageHandlers;

    public void registerHandler(MessageHandler handler) {
        messageHandlers.add(handler);
    }

    private void handleMessage(NotifServerConnection connection, Message message)
    {
        try {
            if (message instanceof Multi) {
                Multi multiMsg = (Multi) message;
                for (int i = 0; i < ((Multi) message).getVector().size(); i++) {
                    handleMessage(connection, (Message) multiMsg.getVector().get(i));
                }
                
            } else {
                for (MessageHandler handler : messageHandlers) {
                    if (handler.supportsMessageType(message)) {
                        handler.handleMessage(connection, message);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Uncaught exception handling " + message + " from " + connection, e);
        }
    }
    
    @Override
    public void messageReceived(MessageEvent messageEvent) {
        NotifServerConnection connection = (NotifServerConnection)messageEvent.getSource();
        Message message = messageEvent.getMessage();
        
        handleMessage(connection, message);
    }
}
