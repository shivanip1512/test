package com.cannontech.core.dynamic.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.tags.AlarmUtils;
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointDataListener;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.SignalListener;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.cache.DBChangeLiteListener;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.ConnStateChange;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.IServerConnection;

/**
 * Implementation of AsyncDynamicDataSource
 * @author alauinger
 */
public class AsyncDynamicDataSourceImpl implements AsyncDynamicDataSource, MessageListener  {
    
    private DispatchProxy dispatchProxy;
    private IServerConnection dispatchConnection;
    private IDatabaseCache databaseCache;
    private DynamicDataSource dynamicDataSource;
    
    private Map<Integer, LinkedHashSet<PointDataListener>> pointIdPointDataListeners =
        new HashMap<Integer, LinkedHashSet<PointDataListener>>();

    private Map<Integer, LinkedHashSet<SignalListener>> pointIdSignalListeners = 
            new HashMap<Integer, LinkedHashSet<SignalListener>>();
    
    private Map<PointDataListener, HashSet<Integer>> pointDataListenerPointIds =
            new HashMap<PointDataListener, HashSet<Integer>>();
    
    private Map<SignalListener, HashSet<Integer>> signalListenerPointIds = 
            new HashMap<SignalListener, HashSet<Integer>>();
    
    private List<SignalListener> alarmSignalListeners = new ArrayList<SignalListener>();

    private Set<DBChangeListener> dbChangeListeners = 
        new HashSet<DBChangeListener>();
    
    private Set<DBChangeLiteListener> dbChangeLiteListeners = 
        new HashSet<DBChangeLiteListener>();

    public void registerForPointData(PointDataListener l, Set<Integer> pointIds) {

        //First register with dispatch point ids as necessary
        //If it throws then we won't have changed any of our state
        dispatchProxy.registerForPointIds(pointIds);
        
        //Associate the point ids with the listener
        // and associate the listener with each of the points ids
        HashSet<Integer> listenerPointIds = pointDataListenerPointIds.get(l);
        if(listenerPointIds == null) {
            listenerPointIds = new HashSet<Integer>();
            pointDataListenerPointIds.put(l, listenerPointIds);
        }
        for (Integer id : pointIds) {
            listenerPointIds.add(id);
            LinkedHashSet<PointDataListener> listeners = pointIdPointDataListeners.get(id);
            if(listeners == null) {
                listeners = new LinkedHashSet<PointDataListener>();
                pointIdPointDataListeners.put(id, listeners);
            }
            listeners.add(l);
        }
    }
    
    public PointValueHolder getAndRegisterForPointData(PointDataListener l, int pointId) {
        registerForPointData(l, Collections.singleton(pointId));
        return dynamicDataSource.getPointValue(pointId);
    }

    public void unRegisterForPointData(PointDataListener l, Set<Integer> pointIds) {
        Set<Integer> listenerPointIds = pointDataListenerPointIds.get(l); 
            if(listenerPointIds != null) {
            for (Integer id : pointIds) {
                Set<PointDataListener> listeners = pointIdPointDataListeners.get(id);
                if(listeners != null) {
                    listeners.remove(l);
                    if(listeners.size() == 0) {
                        pointIdPointDataListeners.remove(id);
                    }
                }
                listenerPointIds.remove(id);
            }
            if(listenerPointIds.size() == 0) {
                pointDataListenerPointIds.remove(l);
            }               
        }
    }

    public void unRegisterForPointData(PointDataListener l) {
        Set<Integer> pointIds = pointDataListenerPointIds.get(l);
        if(pointIds != null) {
        	Set<Integer> copyPointIds = new HashSet<Integer>(pointIds);
            unRegisterForPointData(l, copyPointIds);
        }
    }

    public void registerForSignals(SignalListener l, Set<Integer> pointIds) {

        //First register with dispatch point ids as necessary
        //If it throws then we won't have changed any of our state
        dispatchProxy.registerForPointIds(pointIds);
        
        //Associate the point ids with the listener
        // and associate the listener with each of the points ids
        HashSet<Integer> listenerPointIds = signalListenerPointIds.get(l);
        if(listenerPointIds == null) {
            listenerPointIds = new HashSet<Integer>();
            signalListenerPointIds.put(l, listenerPointIds);
        }
        for (Integer id : pointIds) {
            listenerPointIds.add(id);
            LinkedHashSet<SignalListener> listeners = pointIdSignalListeners.get(id);
            if(listeners == null) {
                listeners = new LinkedHashSet<SignalListener>();
                pointIdSignalListeners.put(id, listeners);
            }
            listeners.add(l);
        }        

    }
    
    @Override
    public void registerForAllAlarms(SignalListener listener) {
        dispatchProxy.registerForAlarms();
        alarmSignalListeners.add(listener);
    }

    public void unRegisterForSignals(SignalListener l, Set<Integer> pointIds) {
        Set<Integer> listenerPointIds = signalListenerPointIds.get(l);
        if(listenerPointIds != null) {
            for (Integer id : pointIds) {
                Set<SignalListener> listeners = pointIdSignalListeners.get(id);
                if(listeners != null) {
                    listeners.remove(l);
                    if(listeners.size() == 0) {
                        pointIdSignalListeners.remove(id);
                    }
                }
                listenerPointIds.remove(id);
            }
            if(listenerPointIds.size() == 0) {
                signalListenerPointIds.remove(l);
            }          
        }
    }

    public void unRegisterForSignals(SignalListener l) {
        Set<Integer> pointIds = new HashSet<Integer>(signalListenerPointIds.get(l));
        if(pointIds != null) {
            unRegisterForSignals(l, pointIds);
        }
    }

    public void addDBChangeListener(DBChangeListener l) {
        dbChangeListeners.add(l);
    }

    public void removeDBChangeListener(DBChangeListener l) {
        dbChangeListeners.remove(l);
    }

    public void addDBChangeLiteListener(DBChangeLiteListener l) {
        dbChangeLiteListeners.add(l);
    }

    public void removeDBChangeLiteListener(DBChangeLiteListener l) {
        dbChangeLiteListeners.remove(l);
    }
        
    public void messageReceived(MessageEvent e) {
        Object o = e.getMessage();
        handleIncoming(o);        
    }

    public void handleIncoming(Object o) {
        if(o instanceof PointData) {
            handlePointData((PointData)o);
        }
        else if(o instanceof Signal) {
            handleSignal((Signal)o);
        }
        else if(o instanceof DBChangeMsg) {
            handleDBChange((DBChangeMsg)o);
        }
        else if(o instanceof ServerResponseMsg) {
            handleIncoming(((ServerResponseMsg)o).getPayload());
        }
        else if(o instanceof Multi) {
            Multi multi = (Multi) o;
            for(Object obj : multi.getVector()) {
                handleIncoming(obj);
            }
        }
        else if(o instanceof ConnStateChange) {
            ConnStateChange csc = (ConnStateChange) o;
            if(csc.isConnected()) {
               reRegisterForEverything(); 
            }
        }
    }
    
    public void handlePointData(PointData pointData) {
        Set<PointDataListener> listeners = pointIdPointDataListeners.get(pointData.getId());
        if(listeners != null) {
            for (PointDataListener listener : listeners) {
                listener.pointDataReceived(pointData);
            }
        }
    }
    
    public void handleSignal(Signal signal) {
        Set<SignalListener> listeners = pointIdSignalListeners.get(signal.getPointID());
        if(listeners != null) {                   
            for (SignalListener listener : listeners) {
                listener.signalReceived(signal);
            }
        }
        
        final int tags = signal.getTags(); 
        boolean isAlarmSignal = TagUtils.isAnyAlarm(tags);
        boolean isNewAlarm = TagUtils.isNewAlarm(tags);
        if (isAlarmSignal && isNewAlarm) {
            for (SignalListener listener : alarmSignalListeners) {
                listener.signalReceived(signal);
            }
        }
    }
    
    public void handleDBChange(DBChangeMsg dbChange) {
        for (DBChangeListener listener : dbChangeListeners) {
            listener.dbChangeReceived(dbChange);
        }
        
        LiteBase lite = databaseCache.handleDBChangeMessage(dbChange);
        for (DBChangeLiteListener listener : dbChangeLiteListeners) {
            listener.handleDBChangeMsg(dbChange, lite);
        }
    }
    /**
     * Reregister with dispatch for every point id
     * a listener is listening for.
     * Useful for when the connection goes down then up again
     */
    private void reRegisterForEverything() {
        int numIds = pointIdPointDataListeners.size()+pointIdSignalListeners.size();
        Set<Integer> pointIdsToRegisterFor = new HashSet<Integer>((int)(numIds/0.75f)+1);
        pointIdsToRegisterFor.addAll(pointIdPointDataListeners.keySet());
        pointIdsToRegisterFor.addAll(pointIdSignalListeners.keySet());

        dispatchProxy.registerForPointIds(pointIdsToRegisterFor);
    }
    
    public void setDispatchProxy(DispatchProxy dispatchProxy) {
        this.dispatchProxy = dispatchProxy;
    }
    
    public void setDispatchConnection(IServerConnection dispatchConnection) {
        // Should we unregister with the old connection?? might leak otherwise?
        if(this.dispatchConnection != null) {
            this.dispatchConnection.removeMessageListener(this);
        }
        this.dispatchConnection = dispatchConnection;
        this.dispatchConnection.addMessageListener(this);
    }

    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }
    
    public Set<Integer> getPointIds(PointDataListener l) {
        Set<Integer> ids = pointDataListenerPointIds.get(l);
        if(ids == null) {
            return Collections.emptySet();
        }        
        return ids;
    }
    
    public Set<Integer> getPointIds(SignalListener l) {
        Set<Integer> ids = signalListenerPointIds.get(l);
        if(ids == null) {
            return Collections.emptySet();
        }
        return ids;
    }
    
    public Set<PointDataListener> getPointDataListeners(int pointId) {
        Set<PointDataListener> listeners = pointIdPointDataListeners.get(pointId);
        if(listeners == null) {
            return Collections.emptySet();
        }
        return listeners;
    }
    
    public Set<SignalListener> getSignalListeners(int pointId) {
        Set<SignalListener> listeners = pointIdSignalListeners.get(pointId);
        if(listeners == null) {
            return Collections.emptySet();
        }
        return listeners;
    }    
    
    @Required
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }
}
