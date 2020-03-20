package com.cannontech.yukon;

import org.springframework.jmx.export.annotation.ManagedAttribute;

import com.cannontech.message.server.ServerRequestMsg;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageListener;

public interface BasicServerConnection {

    public void write(Message o);

    public void writePrivateServerRequest(ServerRequestMsg msg, MessageListener l);

    public void queue(Message o);

    public void addMessageListener(MessageListener l);
    
    public void removeMessageListener(MessageListener l);
    
    public void removePrivateListener(Integer serverRequestId);

    @ManagedAttribute
    public boolean isValid();
}