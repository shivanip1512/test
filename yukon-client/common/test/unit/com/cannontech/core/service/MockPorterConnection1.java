package com.cannontech.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.porter.RequestMessage;
import com.cannontech.messaging.message.porter.ReturnMessage;
import com.cannontech.messaging.util.MessageEvent;
import com.cannontech.messaging.util.MessageListener;
import com.cannontech.yukon.BasicServerConnection;

public class MockPorterConnection1 implements BasicServerConnection {
    List<MessageListener> listeners = new ArrayList<MessageListener>();
    Timer timer = new Timer();

    public void addMessageListener(MessageListener l) {
        listeners.add(l);
    }

    public void queue(BaseMessage o) {
        write(o);
    }

    public void removeMessageListener(MessageListener l) {
        listeners.remove(l);
    }

    public void write(BaseMessage o) {
        if (!(o instanceof RequestMessage)) return;
        final RequestMessage req = (RequestMessage) o;
        CTILogger.info("Received message: " + req);
        // send first response in 15 seconds
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ReturnMessage retMsg = new ReturnMessage();
                retMsg.setExpectMore(true);
                retMsg.setUserMessageId(req.getUserMessageId());
                retMsg.setDeviceId(req.getDeviceId());
                returnMessage(retMsg);
            }
        }, 15000);
        // send first response in 30 seconds
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ReturnMessage retMsg = new ReturnMessage();
                retMsg.setExpectMore(false);
                retMsg.setUserMessageId(req.getUserMessageId());
                retMsg.setDeviceId(req.getDeviceId());
                retMsg.setResultString("Mock porter result");
                returnMessage(retMsg);
            }
        }, 50000);
    }
    
    private void returnMessage(BaseMessage m) {
        CTILogger.info("Sending mock return message: " + m);
        MessageEvent e = new MessageEvent(this, m);
        
        // go backwards to allow listeners to remove 
        // themselves concurrently within the handler
        for(int i = listeners.size()-1; i >= 0; i--) {
            MessageListener listener = listeners.get(i);
            listener.messageReceived(e);
        }
    }

    public boolean isValid() {
        return true;
    }
}
