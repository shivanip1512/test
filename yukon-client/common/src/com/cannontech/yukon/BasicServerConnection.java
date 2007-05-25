package com.cannontech.yukon;

import com.cannontech.message.util.MessageListener;

public interface BasicServerConnection {

    public void write(Object o);

    public void queue(Object o);

    public void addMessageListener(MessageListener l);

    public void removeMessageListener(MessageListener l);

    public boolean isValid();
}