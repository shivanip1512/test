package com.cannontech.dynamic;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.util.MessageListener;
import com.cannontech.yukon.IServerConnection;

public class MockDispatchConnection implements IServerConnection {

    public List<BaseMessage> messagesWritten = new ArrayList<BaseMessage>();
    
    public void write(BaseMessage o) {
        messagesWritten.add(o);
    }
    
    public void queue(BaseMessage o) {
        messagesWritten.add(o);
    }
  
  
    public boolean getAutoReconnect() {
        return false;
    }

    public String getHost() {
        return null;
    }

    public int getNumOutMessages() {
        return 0;
    }

    public int getPort() {
        return 0;
    }

    public int getTimeToReconnect() {
        return 0;
    }

    public boolean isMonitorThreadAlive() {
        return false;
    }

    public boolean isValid() {
        return true;
    }

    public void setAutoReconnect(boolean val) {
    }

  

    public void addMessageListener(MessageListener l) {
    }

    public void removeMessageListener(MessageListener l) {
    }

    public boolean isQueueMessages() {
        return false;
    }

    public void setQueueMessages(boolean b) {
    }

    public void addObserver(Observer obs) {
    }

    public void deleteObserver(Observer obs) {
    }

    public void disconnect() {
    }

}
