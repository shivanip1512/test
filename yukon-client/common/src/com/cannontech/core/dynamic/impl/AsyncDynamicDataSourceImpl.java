package com.cannontech.core.dynamic.impl;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.core.dynamic.AllPointDataListener;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointDataListener;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.SignalListener;
import com.cannontech.core.dynamic.exception.DispatchNotConnectedException;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.cache.DBChangeLiteListener;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeHelper;
import com.cannontech.message.dispatch.message.DbChangeType;
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
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

@ManagedResource
public class AsyncDynamicDataSourceImpl implements AsyncDynamicDataSource, MessageListener  {
    
    private static final Logger log = YukonLogManager.getLogger(AsyncDynamicDataSourceImpl.class);
    
    private DispatchProxy dispatchProxy;
    @Autowired private IServerConnection dispatchConnection;
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private DynamicDataSource dynamicDataSource;
    
    // Note that this is purposefully different than the default com.cannontech.common.util.CtiUtilities.DEFAULT_MSG_SOURCE
    // that is set in the message class because there is already code that attaches
    // specific meaning to that. 
    private String applicationSourceIdentifier = BootstrapUtils.getApplicationName() + "$" + UUID.randomUUID();
    // For example, see 
    // com.cannontech.dbeditor.DatabaseEditor.queueDBChangeMsgs(DBPersistent, int, DBChangeMsg[]).
    // The problem is that the DatabaseEditor sends its own DB Changes, but registers directly with 
    // this class for DB Change notifications. So, when DBEditor sends a DBChange, its old code will 
    // directly handle the message to a certain degree, then it will reflect back from dispatch 
    // through this class, whereby all of the listeners will be invoked INCLUDING the DBEditor 
    // itself. When new code sends a DB Change directly through this code, we can invoke all of the 
    // listeners immediately and then ignore the reflection.
    
    private volatile boolean allPointsRegistered = false;
    private SetMultimap<Integer, PointDataListener> pointIdPointDataListeners;
    private SetMultimap<Integer, SignalListener> pointIdSignalListeners;

    {
        log.info("source id: " + applicationSourceIdentifier);
        
        SetMultimap<Integer, PointDataListener> pointIdPointDataListenersUnsynchronized = LinkedHashMultimap.create();
        pointIdPointDataListeners = Multimaps.synchronizedSetMultimap(pointIdPointDataListenersUnsynchronized);
        
        SetMultimap<Integer, SignalListener> pointIdSignalListenersUnsynchronized = LinkedHashMultimap.create();
        pointIdSignalListeners =Multimaps.synchronizedSetMultimap(pointIdSignalListenersUnsynchronized);
    }
    
    private List<SignalListener> alarmSignalListeners = new CopyOnWriteArrayList<SignalListener>();
    private List<PointDataListener> allPointListeners = new CopyOnWriteArrayList<PointDataListener>();
    
    private Set<DBChangeListener> dbChangeListeners = new CopyOnWriteArraySet<DBChangeListener>();
    private Set<DBChangeLiteListener> dbChangeLiteListeners = new CopyOnWriteArraySet<DBChangeLiteListener>();
    
    @PostConstruct
    public void initialize() {
        this.dispatchConnection.addMessageListener(this);
    }
    
    @Override
    public void registerForAllPointData(PointDataListener l) {
        allPointListeners.add(l);
        allPointsRegistered = true;

        try{
        	dispatchProxy.registerForPoints();	// send registration command
        } catch (DispatchNotConnectedException e) {
            CTILogger.info("Registration failed temporarily because Dispatch wasn't connected");
        }
    }

    @Override
    public void registerForPointData(PointDataListener l, Set<Integer> pointIds) {
        // find points we aren't already registered for
        Set<Integer> unregisteredPointIds = Sets.difference(pointIds, getAllRegisteredPoints()).immutableCopy();
        
        for (Integer id : pointIds) {
            pointIdPointDataListeners.put(id, l);
        }

        if (!allPointsRegistered) {
            try{
                dispatchProxy.registerForPointIds(unregisteredPointIds);
            } catch (DispatchNotConnectedException e) {
                CTILogger.info("Registration failed temporarily because Dispatch wasn't connected");
            }
        }
    }
    
    @Override
    public PointValueQualityHolder getAndRegisterForPointData(PointDataListener l, int pointId) {
        registerForPointData(l, Collections.singleton(pointId));
        return dynamicDataSource.getPointValue(pointId);
    }
    
    @Override
    public Set<? extends PointValueQualityHolder> getAndRegisterForPointData(PointDataListener l, Set<Integer> pointIds) {
        registerForPointData(l, pointIds);
        return dynamicDataSource.getPointValues(pointIds);
    }

    @Override
    public void unRegisterForPointData(PointDataListener l, Set<Integer> pointIds) {
        for (Integer id : pointIds) {
            pointIdPointDataListeners.remove(id, l);
        }
    }

    @Override
    public void unRegisterForPointData(PointDataListener l) {
        pointIdPointDataListeners.values().removeAll(ImmutableSet.of(l));
    }

    @Override
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

    @Override
    public void unRegisterForSignals(SignalListener l, Set<Integer> pointIds) {
        for (Integer id : pointIds) {
            pointIdSignalListeners.remove(id, l);
        }
    }

    @Override
    public void unRegisterForSignals(SignalListener l) {
        pointIdSignalListeners.values().removeAll(ImmutableSet.of(l));
    }

    @Override
    public void addDBChangeListener(DBChangeListener l) {
        dbChangeListeners.add(l);
    }

    @Override
    public void removeDBChangeListener(DBChangeListener l) {
        dbChangeListeners.remove(l);
    }

    @Override
    public void addDBChangeLiteListener(DBChangeLiteListener l) {
        dbChangeLiteListeners.add(l);
    }

    @Override
    public void removeDBChangeLiteListener(DBChangeLiteListener l) {
        dbChangeLiteListeners.remove(l);
    }
    
    @Override
    public void addDatabaseChangeEventListener(final DatabaseChangeEventListener listener) {
        addDBChangeListener(new DBChangeListener() {
            @Override
            public void dbChangeReceived(DBChangeMsg dbChange) {
                DatabaseChangeEvent event = DbChangeHelper.convertToEvent(dbChange);
                if (event != null) {
                    listener.eventReceived(event);
                }
            }
        });
    }
    
    @Override
    public void addDatabaseChangeEventListener(final DbChangeCategory changeCategory, final DatabaseChangeEventListener listener) {
        addDatabaseChangeEventListener(changeCategory, EnumSet.allOf(DbChangeType.class), listener);
    }

    @Override
    public void addDatabaseChangeEventListener(final DbChangeCategory changeCategory,
                                                final Set<DbChangeType> types,
                                                final DatabaseChangeEventListener listener) {
        addDBChangeListener(new DBChangeListener() {
            @Override
            public void dbChangeReceived(DBChangeMsg dbChange) {
                DatabaseChangeEvent event = DbChangeHelper.findMatchingEvent(dbChange, types, changeCategory);
                if (event != null) {
                    listener.eventReceived(event);
                }
            }
        });
    }

    @Override
    public void messageReceived(MessageEvent e) {
        Object o = e.getMessage();
        handleIncoming(o);
    }

    private void handleIncoming(Object o) {
        if (o instanceof PointData) {
            handlePointData((PointData)o);
        } else if (o instanceof Signal) {
            handleSignal((Signal)o);
        } else if (o instanceof DBChangeMsg) {
            handleDBChange((DBChangeMsg)o);
        } else if (o instanceof ServerResponseMsg) {
            handleIncoming(((ServerResponseMsg)o).getPayload());
        } else if (o instanceof Multi<?>) {
            Multi<?> multi = (Multi<?>) o;
            for (Object obj : multi.getVector()) {
                handleIncoming(obj);
            }
        } else if (o instanceof ConnStateChange) {
            ConnStateChange csc = (ConnStateChange) o;
            if (csc.isConnected()) {
               reRegisterForEverything(); 
            }
        }
    }
    
    public void handlePointData(PointData pointData) {
        Set<PointDataListener> listeners = Sets.newHashSet(allPointListeners);
        // lock pointIdPointDataListeners (the object is the lock used internally)
        synchronized (pointIdPointDataListeners) {
            Set<PointDataListener> pointIdListeners = pointIdPointDataListeners.get(pointData.getId());
            listeners.addAll(pointIdListeners);
        }
        
        // make sure we release the lock before calling the listeners
        for (PointDataListener listener : listeners) {
            boolean newData = !pointData.getTagsOldTimestamp();
            boolean listenerWantsAll = listener instanceof AllPointDataListener;
            if (newData || listenerWantsAll ) {
                listener.pointDataReceived(pointData);
            }
        }
    }
    
    private void handleSignal(Signal signal) {
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
        // Don't process message if we know it was sent through
        // our own publishDbChange method. 
        if (!dbChange.getSource().equals(applicationSourceIdentifier)) {
            processDbChange(dbChange);
        }
    }

    private void processDbChange(DBChangeMsg dbChange) {
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
    
    @Override
    public void publishDbChange(DBChangeMsg dbChange) {
        // set the source here so that when the changes comes back 
        // through this class we know that it must have started
        // in this method
        dbChange.setSource(applicationSourceIdentifier);
        processDbChange(dbChange);
        dispatchConnection.queue(dbChange);
    }

    /**
     * Reregister with dispatch for every point id
     * a listener is listening for.
     * Useful for when the connection goes down then up again
     */
    private void reRegisterForEverything() {
        if (allPointsRegistered) {
            dispatchProxy.registerForPoints();
        } else {
            Set<Integer> union = getAllRegisteredPoints();

            dispatchProxy.registerForPointIds(union);
        }
    }

    private Set<Integer> getAllRegisteredPoints() {
        return Sets.union(pointIdPointDataListeners.keySet(), pointIdSignalListeners.keySet());
    }
    
    @ManagedAttribute
    public boolean isAllPointsRegistered() {
        return allPointsRegistered;
    }
    
    @ManagedAttribute
    public int getRegisteredPointCount() {
        if (allPointsRegistered) {
            return -1;
        } else {
            return getAllRegisteredPoints().size();
        }
    }
    
    @ManagedAttribute
    public int getPointListenerCount() {
        return pointIdPointDataListeners.values().size() + allPointListeners.size();
    }
    
    @ManagedAttribute
    public int getSignalListenerCount() {
        return pointIdSignalListeners.values().size() + alarmSignalListeners.size();
    }
    
    @ManagedAttribute
    public int getDbChangeLiteListenerCount() {
        return dbChangeLiteListeners.size();
    }
    
    @ManagedAttribute
    public int getDbChangeListenerCount() {
        return dbChangeListeners.size();
    }
    
    public void setDispatchProxy(DispatchProxy dispatchProxy) {
        this.dispatchProxy = dispatchProxy;
    }
    
    public Set<PointDataListener> getPointDataListeners(int pointId) {
        Set<PointDataListener> listeners = pointIdPointDataListeners.get(pointId);
        return listeners;
    }
    
    public Set<SignalListener> getSignalListeners(int pointId) {
        Set<SignalListener> listeners = pointIdSignalListeners.get(pointId);
        return listeners;
    }
    
}