package com.cannontech.core.dynamic.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.common.point.PointQuality;
import com.cannontech.message.dispatch.message.LitePointData;
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
 */
/* package */ class DynamicDataCache implements MessageListener {
    // Stores current PointData messages by PointID
    private Map<Integer, LitePointData> pointData = new ConcurrentHashMap<>();

    // Stores current Signal messages by PointID
    // Only signals with a category > 1 will be stored
    private Map<Integer, Set<Signal>> pointSignals = new ConcurrentHashMap<>();

    // Stores the current Signal message by Alarm Category ID
    private Map<Integer, Set<Signal>> categorySignalsMap = new ConcurrentHashMap<>();

    @Autowired
    public DynamicDataCache(@Qualifier("dispatch") IServerConnection dispatchConnection) {
        dispatchConnection.addMessageListener(this);
    }

    LitePointData getPointData(int pointId) {
       return pointData.get(pointId);
    }

    Set<Signal> getSignals(int pointId) {
        Set<Signal> ret = pointSignals.get(pointId);
        return ret;
    }

    Set<Signal> getSignalForCategory(int categoryId) {
        return categorySignalsMap.get(categoryId);
    }

    @Override
    public void messageReceived(MessageEvent e) {
        handleIncoming(e.getMessage());
    }

    void handleIncoming(Object msg) {
        if (msg instanceof PointData) {
            handlePointData((PointData) msg);
        } else if (msg instanceof Signal) {
            handleSignal((Signal) msg);
        } else if (msg instanceof ServerResponseMsg) {
            handleIncoming(((ServerResponseMsg) msg).getPayload());
        } else if (msg instanceof Multi) {
            for (Object obj : ((Multi<?>) msg).getVector()) {
                handleIncoming(obj);
            }
        } else if (msg instanceof ConnStateChange) {
            ConnStateChange csc = (ConnStateChange) msg;
            if (!csc.isConnected()) {
                releaseCache();
            }
        }
    }

    /**
     * Add pd to pointData if it is not already in cache OR is after or equals the already cached entry's time stamp
     */
    private void handlePointData(PointData pd) {
        LitePointData cachedPointData = getPointData(pd.getId());
        if (cachedPointData != null
            && cachedPointData.getPointQuality() != PointQuality.Uninitialized
            && cachedPointData.getPointDataTimeStamp().after(pd.getPointDataTimeStamp())) {
            return;
        }
        pointData.put(pd.getId(), LitePointData.of(pd));
    }

    public void handleSignals(Set<Signal> signals, int pointId) {
        if (signals.isEmpty()) {
            Set<Signal> pSignals = new HashSet<Signal>();
            pointSignals.put(pointId, pSignals);
        } else {
            for (Signal signal : signals) {
                handleSignal(signal);
            }
        }
    }

    private void handleSignal(Signal signal) {
        LitePointData pointData = getPointData(signal.getPointID());
        if (pointData != null) {
            pointData.setTags((signal.getTags() & ~Signal.MASK_ANY_ALARM)
                | (pointData.getTags() & Signal.MASK_ANY_ALARM));
        }
        int pointId = signal.getPointID();
        int categoryId = (int) signal.getCategoryID();

        Set<Signal> pSignals = pointSignals.get(pointId);
        if (pSignals == null) {
            pSignals = new HashSet<Signal>();
            pointSignals.put(pointId, pSignals);
        }
        Set<Signal> cSignals = categorySignalsMap.get(categoryId);
        if (cSignals == null) {
            cSignals = new HashSet<Signal>();
            categorySignalsMap.put(categoryId, cSignals);
        }

        pSignals.remove(signal);
        cSignals.remove(signal);

        // Only store the signal if the top two bits indicate alarm activity or a conidition is active
        if ((signal.getTags() & Signal.MASK_ANY_ALARM) != 0
            || (signal.getTags() & Signal.MASK_ANY_ACTIVE_CONDITION) != 0) {
            pSignals.add(signal);
            cSignals.add(signal);
        }
    }

    private void releaseCache() {
        pointData.clear();
        pointSignals.clear();
        categorySignalsMap.clear();
    }
}
