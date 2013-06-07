package com.cannontech.yukon;

import org.springframework.jmx.export.annotation.ManagedAttribute;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.util.MessageListener;

public interface BasicServerConnection {

    public void write(BaseMessage o);

    public void queue(BaseMessage o);

    public void addMessageListener(MessageListener l);

    public void removeMessageListener(MessageListener l);

    @ManagedAttribute
    public boolean isValid();
}