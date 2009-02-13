package com.cannontech.core.dynamic.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointDataListener;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.SignalListener;
import com.cannontech.core.dynamic.exception.DispatchNotConnectedException;
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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

/**
 * Implementation of AsyncDynamicDataSource
 */
public class AsyncDynamicDataSourceImpl implements AsyncDynamicDataSource, MessageListener  {
    
    private DispatchProxy dispatchProxy;
    private IServerConnection dispatchConnection;
    private IDatabaseCache databaseCache;
    private DynamicDataSource dynamicDataSource;
    
    private SetMultimap<Integer, PointDataListener> pointIdPointDataListeners;
    private SetMultimap<Integer, SignalListener> pointIdSignalListeners;

    {
        SetMultimap<Integer, PointDataListener> pointIdPointDataListenersUnsynchronized = Multimaps.newLinkedHashMultimap();
        pointIdPointDataListeners = Multimaps.synchronizedSetMultimap(pointIdPointDataListenersUnsynchronized);
        
        SetMultimap<Integer, SignalListener> pointIdSignalListenersUnsynchronized = Multimaps.newLinkedHashMultimap();
        pointIdSignalListeners =Multimaps.synchronizedSetMultimap(pointIdSignalListenersUnsynchronized);
    }
    
    
    private List<SignalListener> alarmSignalListeners = new CopyOnWriteArrayList<SignalListener>();

    private Set<DBChangeListener> dbChangeListeners = new CopyOnWriteArraySet<DBChangeListener>();
    
    private Set<DBChangeLiteListener> dbChangeLiteListeners = new CopyOnWriteArraySet<DBChangeLiteListener>();
    
    @PostConstruct
    public void initialize() {
        this.dispatchConnection.addMessageListener(this);
    }

    public void registerForPointData(PointDataListener l, Set<Integer> pointIds) {
        // find points we aren't already registered for
        Set<Integer> unregisteredPointIds = Sets.difference(pointIds, getAllRegisteredPoints()).immutableCopy();
        
        for (Integer id : pointIds) {
            pointIdPointDataListeners.put(id, l);
        }

        try{
        	dispatchProxy.registerForPointIds(unregisteredPointIds);
        } catch (DispatchNotConnectedException e) {
        	CTILogger.info("Registration failed temporarily because Dispatch wasn't connected");
        }
    }
    
    public PointValueQualityHolder getAndRegisterForPointData(PointDataListener l, int pointId) {
        registerForPointData(l, Collections.singleton(pointId));
        return dynamicDataSource.getPointValue(pointId);
    }

    public void unRegisterForPointData(PointDataListener l, Set<Integer> pointIds) {
        for (Integer id : pointIds) {
            pointIdPointDataListeners.remove(id, l);
        }
    }

    public void unRegisterForPointData(PointDataListener l) {
        pointIdPointDataListeners.values().removeAll(ImmutableSet.of(l));
    }

    public void registerForSignals(SignalListener l, Set<Integer> pointIds) {
        // find points we aren't already registered for
        Set<Integer> unregisteredPointIds = Sets.difference(pointIds, getAllRegisteredPoints()).immutableCopy();
        
        for (Integer id : pointIds) {
            pointIdSignalListeners.put(id, l);
        }        

        try{
	    	dispatchProxy.registerForPointIds(unregisteredPointIds);
	    } catch (DispatchNotConnectedException e) {
	    	CTILogger.info("Registration failed temporarily because Dispatch wasn't connected");
	    } 

    }
    
    @Override
    public void registerForAllAlarms(SignalListener listener) {
        dispatchProxy.registerForAlarms();
        alarmSignalListeners.add(listener);
    }

    public void unRegisterForSignals(SignalListener l, Set<Integer> pointIds) {
        for (Integer id : pointIds) {
            pointIdSignalListeners.remove(id, l);
        }
    }

    public void unRegisterForSignals(SignalListener l) {
        pointIdSignalListeners.values().removeAll(ImmutableSet.of(l));
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
        Set<PointDataListener> listeners;
        // lock pointIdPointDataListeners (the object is the lock used internally)
        synchronized (pointIdPointDataListeners) {
            listeners = pointIdPointDataListeners.get(pointData.getId());
            listeners = ImmutableSet.copyOf(listeners);
        }
        // make sure we release the lock before calling the listeners
        for (PointDataListener listener : listeners) {
            listener.pointDataReceived(pointData);
        }
    }
    
    public void handleSignal(Signal signal) {
        Set<SignalListener> listeners;
        // lock pointIdSignalListeners (the object is the lock used internally)
        synchronized (pointIdSignalListeners) {
            listeners = pointIdSignalListeners.get(signal.getPointID());
            listeners = ImmutableSet.copyOf(listeners);
        }
        // make sure we release the lock before calling the listeners
        for (SignalListener listener : listeners) {
            listener.signalReceived(signal);
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
    
    
    private void handleDBChange(DBChangeMsg dbChange) {
        // the following would be a nice check some day
        
//        if (!dbChange.getSource().equals(CtiUtilities.DEFAULT_MSG_SOURCE)) {
//            handleInternalDBChange(dbChange);
//        }
        
        // the databaseCache call usually happened after the dbChangeListeners were processed
        // but, because several dbChangeLiteListeners have been converted to dbChangeListeners
        // it seems like a good idea to ensure things still happen in the expected order
        
        boolean noObjectNeeded = dbChangeLiteListeners.isEmpty();
        LiteBase lite = databaseCache.handleDBChangeMessage(dbChange, noObjectNeeded);
        
        for (DBChangeListener listener : dbChangeListeners) {
            listener.dbChangeReceived(dbChange);
        }
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
        Set<Integer> union = getAllRegisteredPoints();

        dispatchProxy.registerForPointIds(union);
    }

    private Set<Integer> getAllRegisteredPoints() {
        return Sets.union(pointIdPointDataListeners.keySet(), pointIdSignalListeners.keySet());
    }
    
    public void setDispatchProxy(DispatchProxy dispatchProxy) {
        this.dispatchProxy = dispatchProxy;
    }
    
    public void setDispatchConnection(IServerConnection dispatchConnection) {
        this.dispatchConnection = dispatchConnection;
    }

    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }
    
    public Set<PointDataListener> getPointDataListeners(int pointId) {
        Set<PointDataListener> listeners = pointIdPointDataListeners.get(pointId);
        return listeners;
    }
    
    public Set<SignalListener> getSignalListeners(int pointId) {
        Set<SignalListener> listeners = pointIdSignalListeners.get(pointId);
        return listeners;
    }    
    
    @Required
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }
}
