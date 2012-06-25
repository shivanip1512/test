package com.cannontech.core.dynamic.impl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.ConnStateChange;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
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
    private Map<Integer, PointData> pointData = 
        new ConcurrentHashMap<Integer, PointData>();
    
    // Stores current Signal messages by PointID
    // Only signals with a category > 1 will be stored
    private Map<Integer, Set<Signal>> pointSignals = 
        new ConcurrentHashMap<Integer, Set<Signal>>();
    
    // Stores the current Signal message by Alarm Category ID
    private Map<Integer, Set<Signal>> categorySignalsMap = 
        new ConcurrentHashMap<Integer, Set<Signal>>();
    
    PointData getPointData(int pointId) {
        return pointData.get(pointId);
    }
    
    Set<Signal> getSignals(int pointId) {
        
        Set<Signal> ret = pointSignals.get(pointId);
        return ret;
    }
    
    Set<Signal> getSignalForCategory(int categoryId) {
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
        if(msg instanceof PointData) {
            handlePointData((PointData)msg);
        }
        else if(msg instanceof Signal) {
            handleSignal((Signal)msg);
        }
        else if(msg instanceof ServerResponseMsg) {
            handleIncoming(((ServerResponseMsg)msg).getPayload());
        }
        else if(msg instanceof Multi) {
            for(Object o: ((Multi)msg).getVector()) {
                handleIncoming(o);
            }
        }
        else if(msg instanceof ConnStateChange) {
            ConnStateChange csc = (ConnStateChange) msg;
            if(!csc.isConnected()) {
                releaseCache();
            }
        }
    }
    
    private void handlePointData(PointData pd) {
        pointData.put(pd.getId(), pd);
    }
    
    public void handleSignals(Set<Signal> signals, int pointId) {
        if(signals.isEmpty()) {
            Set<Signal> pSignals = new HashSet<Signal>();
            pointSignals.put(pointId, pSignals);
        }else {
            for(Signal signal : signals) {
                handleSignal(signal);
            }
        }
    }
    
    private void handleSignal(Signal signal) {
        int pointId = signal.getPointID();
        int categoryId = (int)signal.getCategoryID();
        
        Set<Signal> pSignals = pointSignals.get(pointId);
        if(pSignals == null) {
            pSignals = new HashSet<Signal>();
            pointSignals.put(pointId, pSignals);
        }
        Set<Signal> cSignals = categorySignalsMap.get(categoryId);
        if(cSignals == null) {
            cSignals = new HashSet<Signal>();
            categorySignalsMap.put(categoryId, cSignals);
        }
        
        pSignals.remove(signal);
        cSignals.remove(signal);
        
        // Only store the signal if the top two bits indicate alarm activity or a conidition is active
        if((signal.getTags() & Signal.MASK_ANY_ALARM) != 0 ||
                (signal.getTags() & Signal.MASK_ANY_ACTIVE_CONDITION) != 0) {
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
