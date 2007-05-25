package com.cannontech.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.yukon.BasicServerConnection;

public class MockPorterConnection implements BasicServerConnection {
    List<MessageListener> listeners = new ArrayList<MessageListener>();
    Timer timer = new Timer();

    public void addMessageListener(MessageListener l) {
        listeners.add(l);
    }

    public void queue(Object o) {
        write(o);
    }

    public void removeMessageListener(MessageListener l) {
        listeners.remove(l);
    }

    public void write(Object o) {
        if (!(o instanceof Request)) return;
        final Request req = (Request) o;
        // send first response in 15 seconds
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Return retMsg = new Return();
                retMsg.setExpectMore(1);
                retMsg.setUserMessageID(req.getUserMessageID());
                retMsg.setDeviceID(req.getDeviceID());
                returnMessage(retMsg);
            }
        }, 15000);
        // send first response in 30 seconds
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Return retMsg = new Return();
                retMsg.setExpectMore(0);
                retMsg.setUserMessageID(req.getUserMessageID());
                retMsg.setDeviceID(req.getDeviceID());
                returnMessage(retMsg);
            }
        }, 50000);
    }
    
    private void returnMessage(Message m) {
        CTILogger.info("Sending mock return message: " + m);
        MessageEvent e = new MessageEvent(this, m);
        for (MessageListener listener : listeners) {
            listener.messageReceived(e);
        }
    }

    public boolean isValid() {
        return true;
    }
}
