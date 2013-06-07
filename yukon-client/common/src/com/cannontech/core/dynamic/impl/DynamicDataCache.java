package com.cannontech.core.dynamic.impl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.cannontech.messaging.message.ConnStateChangeMessage;
import com.cannontech.messaging.message.dispatch.MultiMessage;
import com.cannontech.messaging.message.dispatch.PointDataMessage;
import com.cannontech.messaging.message.dispatch.SignalMessage;
import com.cannontech.messaging.message.server.ServerResponseMessage;
import com.cannontech.messaging.util.MessageEvent;
import com.cannontech.messaging.util.MessageListener;
import com.cannontech.yukon.IServerConnection;

/**
 * DynamicDataCache listens to a dispatch connection and caches
 * PointData and Signal messages.
 * 
 * When the dispatch connection indicates it has disconnected,
 * DynamicDataCache will dump its state and start over to avoid
 * holding onto stale data.
 * @author alauinger
 *
 */
class DynamicDataCache implements MessageListener {
    IServerConnection dispatchConnection;
    
    //  Stores current PointData messages by PointID
    private Map<Integer, PointDataMessage> pointData = 
        new ConcurrentHashMap<Integer, PointDataMessage>();
    
    // Stores current Signal messages by PointID
    // Only signals with a category > 1 will be stored
    private Map<Integer, Set<SignalMessage>> pointSignals = 
        new ConcurrentHashMap<Integer, Set<SignalMessage>>();
    
    // Stores the current Signal message by Alarm Category ID
    private Map<Integer, Set<SignalMessage>> categorySignalsMap = 
        new ConcurrentHashMap<Integer, Set<SignalMessage>>();
    
    PointDataMessage getPointData(int pointId) {
        return pointData.get(pointId);
    }
    
    Set<SignalMessage> getSignals(int pointId) {
        
        Set<SignalMessage> ret = pointSignals.get(pointId);
        return ret;
    }
    
    Set<SignalMessage> getSignalForCategory(int categoryId) {
        return categorySignalsMap.get(categoryId);
    }
    
    int getTags(int pointId) {
        return (int)pointData.get(pointId).getTags();
    }
    
    void removePointId(int pointId) {
        pointData.remove(pointId);
        pointSignals.remove(pointId);
    }
    
    public void clearCache() {
        pointData.clear();
        pointSignals.clear();
        categorySignalsMap.clear();
    }

    @Override
    public void messageReceived(MessageEvent e) {
        handleIncoming(e.getMessage());
    }
    
    
    void handleIncoming(Object msg) {        
        if(msg instanceof PointDataMessage) {
            handlePointData((PointDataMessage)msg);
        }
        else if(msg instanceof SignalMessage) {
            handleSignal((SignalMessage)msg);
        }
        else if(msg instanceof ServerResponseMessage) {
            handleIncoming(((ServerResponseMessage)msg).getPayload());
        }
        else if(msg instanceof MultiMessage) {
            for(Object o: ((MultiMessage)msg).getVector()) {
                handleIncoming(o);
            }
        }
        else if(msg instanceof ConnStateChangeMessage) {
            ConnStateChangeMessage csc = (ConnStateChangeMessage) msg;
            if(!csc.isConnected()) {
                releaseCache();
            }
        }
    }
    
    private void handlePointData(PointDataMessage pd) {
        pointData.put(pd.getId(), pd);
    }
    
    public void handleSignals(Set<SignalMessage> signals, int pointId) {
        if(signals.isEmpty()) {
            Set<SignalMessage> pSignals = new HashSet<SignalMessage>();
            pointSignals.put(pointId, pSignals);
        }else {
            for(SignalMessage signal : signals) {
                handleSignal(signal);
            }
        }
    }
    
    private void handleSignal(SignalMessage signal) {
        int pointId = signal.getPointId();
        int categoryId = (int)signal.getCategoryId();
        
        Set<SignalMessage> pSignals = pointSignals.get(pointId);
        if(pSignals == null) {
            pSignals = new HashSet<SignalMessage>();
            pointSignals.put(pointId, pSignals);
        }
        Set<SignalMessage> cSignals = categorySignalsMap.get(categoryId);
        if(cSignals == null) {
            cSignals = new HashSet<SignalMessage>();
            categorySignalsMap.put(categoryId, cSignals);
        }
        
        pSignals.remove(signal);
        cSignals.remove(signal);
        
        // Only store the signal if the top two bits indicate alarm activity or a conidition is active
        if((signal.getTags() & SignalMessage.MASK_ANY_ALARM) != 0 ||
                (signal.getTags() & SignalMessage.MASK_ANY_ACTIVE_CONDITION) != 0) {
            pSignals.add(signal);
            cSignals.add(signal);
        }
    }
    
    private void releaseCache() {
        pointData.clear();
        pointSignals.clear();
        categorySignalsMap.clear();        
    }
    
    public void setDispatchConnection(IServerConnection dispatchConnection) {
        if(dispatchConnection != null) {
            dispatchConnection.removeMessageListener(this);
        }
        this.dispatchConnection = dispatchConnection;
        dispatchConnection.addMessageListener(this);
    }
}
