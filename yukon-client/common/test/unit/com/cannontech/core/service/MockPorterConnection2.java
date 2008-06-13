package com.cannontech.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.yukon.BasicServerConnection;

public class MockPorterConnection2 implements BasicServerConnection, InitializingBean {
    List<MessageListener> listeners = new ArrayList<MessageListener>();
    Timer timer = new Timer();
    LinkedBlockingQueue<Return> outputQueue = new LinkedBlockingQueue<Return>();
    private ScheduledExecutor scheduledExecutor;

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
        CTILogger.info("Received message: " + req);
        Return retMsg1 = new Return();
        retMsg1.setExpectMore(1);
        retMsg1.setUserMessageID(req.getUserMessageID());
        retMsg1.setDeviceID(req.getDeviceID());
        outputQueue.add(retMsg1);
        
        Return retMsg2 = new Return();
        retMsg2.setExpectMore(0);
        retMsg2.setUserMessageID(req.getUserMessageID());
        retMsg2.setDeviceID(req.getDeviceID());
        retMsg2.setResultString("Mock porter result");
        retMsg2.setStatus(getErrorCode());
        outputQueue.add(retMsg2);
    }
    
    private int getErrorCode() {
        final int[] validCodes = {15,16,17,22,31,36,46,68,72,89,90,91,999999};
        final float errorPercent = 13f;
        
        float f = RandomUtils.nextFloat();
        if (f * 100 < errorPercent) {
            int i = RandomUtils.nextInt(validCodes.length);
            return validCodes[i];
        } else {
            return 0;
        }
    }
    
    private void returnMessage(Message m) {
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

    @Override
    public void afterPropertiesSet() throws Exception {
        scheduledExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Return poll = outputQueue.poll();
                if (poll != null) returnMessage(poll);
            }
            
        }, 0, 6, TimeUnit.SECONDS);
    }
    
    @Autowired
    public void setScheduledExecutor(@Qualifier("main") ScheduledExecutor scheduledExecutor) {
        this.scheduledExecutor = scheduledExecutor;
    }
}
