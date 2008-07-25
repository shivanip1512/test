package com.cannontech.dynamic;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageListener;
import com.cannontech.yukon.IServerConnection;

public class MockDispatchConnection implements IServerConnection {

    public List<Message> messagesWritten = new ArrayList<Message>();
    
    public void write(Message o) {
        messagesWritten.add(o);
    }
    
    public void queue(Message o) {
        messagesWritten.add(o);
    }
  
  
    public boolean getAutoReconnect() {
        return false;
    }

    public String getHost() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getNumOutMessages() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getPort() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getTimeToReconnect() {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean isMonitorThreadAlive() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isValid() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setAutoReconnect(boolean val) {
        // TODO Auto-generated method stub

    }

  

    public void addMessageListener(MessageListener l) {
       // messageListeners.add(l);

    }

    public void removeMessageListener(MessageListener l) {
      //  messageListeners.remove(l);

    }

    public boolean isQueueMessages() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setQueueMessages(boolean b) {
        // TODO Auto-generated method stub

    }

    public void addObserver(Observer obs) {
        // TODO Auto-generated method stub

    }

    public void deleteObserver(Observer obs) {
        // TODO Auto-generated method stub

    }

    public void disconnect() {
    }
    
    

}
