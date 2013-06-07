package com.cannontech.notif.server;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.message.util.*;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.dispatch.MultiMessage;
import com.cannontech.messaging.util.MessageEvent;
import com.cannontech.messaging.util.MessageListener;
import com.cannontech.notif.handler.MessageHandler;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Handles messages for the Notification Server.
 */
public class NotificationMessageHandler implements MessageListener
{
    private static final Logger log = YukonLogManager.getLogger(NotificationMessageHandler.class);
        
    //Built by Autowired Setter
    private Map<Class<? extends BaseMessage>, MessageHandler<?>> messageHandlerMap;
    
    private void handleMessage(NotifServerConnection connection, BaseMessage message) {
        try {
            if (message instanceof MultiMessage) {
                MultiMessage multiMsg = (MultiMessage) message;
                for (int i = 0; i < ((MultiMessage) message).getVector().size(); i++) {
                    handleMessage(connection, (BaseMessage) multiMsg.getVector().get(i));
                }
            } else {
                MessageHandler<?> handler = messageHandlerMap.get(message.getClass());
                
                if (handler != null) {
                    handler.handleMessage(connection, message);
                } //Else unhandled message type. Skip it.
            }
        } catch (Exception e) {
            log.error("Uncaught exception handling " + message + " from " + connection, e);
        }
    }
    
    @Override
    public void messageReceived(MessageEvent messageEvent) {
        NotifServerConnection connection = (NotifServerConnection)messageEvent.getSource();
        BaseMessage message = messageEvent.getMessage();
        
        handleMessage(connection, message);
    }
    
    @Autowired
    public void setMessageHandlers(List<MessageHandler<?>> messageHandlers) {
        /* Build Handler Map */        
        Builder<Class<? extends BaseMessage>, MessageHandler<?>> builder = ImmutableMap.builder();
        
        for (MessageHandler<?> handler : messageHandlers) {
            builder.put(handler.getSupportedMessageType(), handler);
        }
        
        messageHandlerMap = builder.build();        
    }
}
