package com.cannontech.core.dynamic.impl;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.common.stream.StreamUtils;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.core.dynamic.AllPointDataListener;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.core.dynamic.PointDataListener;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.PointValueQualityTagHolder;
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
import com.cannontech.message.dispatch.message.LitePointData;
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
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

@ManagedResource
public class AsyncDynamicDataSourceImpl implements AsyncDynamicDataSource, MessageListener  {

    private static final Logger log = YukonLogManager.getLogger(AsyncDynamicDataSourceImpl.class);

    @Autowired private DispatchProxy dispatchProxy;
    @Autowired private IServerConnection dispatchConnection;
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private DynamicDataCache dynamicDataCache;

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
    private SetMultimap<Integer, PointDataListener> pointIdPointDataListeners = Multimaps.synchronizedSetMultimap(LinkedHashMultimap.create());
    private SetMultimap<Integer, SignalListener> pointIdSignalListeners = Multimaps.synchronizedSetMultimap(LinkedHashMultimap.create());

    private List<SignalListener> alarmSignalListeners = new CopyOnWriteArrayList<>();
    private List<PointDataListener> allPointListeners = new CopyOnWriteArrayList<>();

    private Set<DBChangeListener> dbChangeListeners = new CopyOnWriteArraySet<>();
    private Set<DBChangeLiteListener> dbChangeLiteListeners = new CopyOnWriteArraySet<>();

    @PostConstruct
    public void initialize() {
        log.info("source id: " + applicationSourceIdentifier);
        this.dispatchConnection.addMessageListener(this);
    }

    @Override
    public void putValue(PointData pointData) {
        putValues(Lists.newArrayList(pointData));
    }
    
    @Override
    public void putValues(Iterable<PointData> pointDatas){
        dispatchProxy.putPointData(pointDatas);
    }
    
    @Override
    public void registerForAllPointData(PointDataListener l) {
        allPointListeners.add(l);
        allPointsRegistered = true;

        try{
        	dispatchProxy.registerForPoints();	// send registration command
        } catch (DispatchNotConnectedException e) {
            log.info("Registration failed temporarily because Dispatch wasn't connected");
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
                log.info("Registration failed temporarily because Dispatch wasn't connected");
            }
        }
    }

    @Override
    public Set<? extends PointValueQualityHolder> getAndRegisterForPointData(PointDataListener l, Set<Integer> pointIds) {
        registerForPointData(l, pointIds);
        return getPointValues(pointIds);
    }
    
    @Override
    public Set<? extends PointValueQualityHolder> getPointValues(Set<Integer> pointIds){
        return getPointData(pointIds);
    }
    
    @Override
    public Set<? extends PointValueQualityTagHolder> getPointValuesAndTags(Set<Integer> pointIds){
        return getPointData(pointIds);
    }
    
    @Override
    public long getTags(int pointId) {
        return getPointValueAndTags(pointId).getTags();
    }
      
    @Override
    public PointValueQualityHolder getPointValue(int pointId){
        return getPointData(pointId);
    }
    
    @Override
    public PointValueQualityTagHolder getPointValueAndTags(int pointId){
        return getPointData(pointId);
    }
        
    @Override
    public Set<Signal> getSignals(int pointId) {
        
        Set<Signal> signals = dynamicDataCache.getSignals(pointId);
        if (signals == null) {
            signals = dispatchProxy.getSignals(pointId);
            dynamicDataCache.handleSignals(signals, pointId);
        }
        
        return new HashSet<>(signals);
    }
    
    @Override
    public Set<Signal> getCachedSignals(int pointId) {
        
        Set<Signal> signals = dynamicDataCache.getSignals(pointId);
        if (signals != null) {
           return signals;
        }
        return new HashSet<>();
    }

    @Override
    public Map<Integer, Set<Signal>> getSignals(Set<Integer> pointIds) {
        
        Set<Integer> notCachedPointIds = new HashSet<>(pointIds);
        Map<Integer, Set<Signal>> signals = new HashMap<>((int)(pointIds.size() / 0.75f ) + 1);
        
        // Get whatever we can out of the cache first
        for (Integer id : pointIds) {
            Set<Signal> s = dynamicDataCache.getSignals(id);
            if (s != null) {
                signals.put(id, s);
                notCachedPointIds.remove(id);
            }
        }
        
        // Request to dispatch for the rest
        if (!notCachedPointIds.isEmpty()){
            Map<Integer, Set<Signal>> retrievedSignals = dispatchProxy.getSignals(notCachedPointIds);
            signals.putAll(retrievedSignals);
            for (Integer pointId : notCachedPointIds) {
                Set<Signal> pointSignals = retrievedSignals.get(pointId);
                if (pointSignals == null) {
                    pointSignals = Collections.emptySet();
                }
                dynamicDataCache.handleSignals(pointSignals, pointId);
            }
        }
        
        return signals;
    }
    
    @Override
    public Set<Signal> getSignalsByCategory(int alarmCategoryId) {
        Set<Signal> signals = dynamicDataCache.getSignalForCategory(alarmCategoryId);
        if (signals == null) {
            signals = new HashSet<>();
            Set<Signal> sigSet = dispatchProxy.getSignalsByCategory(alarmCategoryId);
            if (sigSet != null && !sigSet.isEmpty()) {
                signals.addAll(sigSet);
            }
        }
        return signals;
    }
    
    @Override
    public Set<Signal> getCachedSignalsByCategory(int alarmCategoryId) {
        Set<Signal> signals = dynamicDataCache.getSignalForCategory(alarmCategoryId);
        if (signals != null) {
            return signals;
        }
        return new HashSet<>();
    }
    
    @Override
    public void unRegisterForPointData(PointDataListener l, Set<Integer> pointIds) {
        log.debug("[Remove Listener]:"+l.getClass()+" point ids="+pointIds);
        for (Integer id : pointIds) {
            pointIdPointDataListeners.remove(id, l);
        }
    }

    @Override
    public void unRegisterForPointData(PointDataListener l) {
        log.debug("[Remove Listener]:"+l.getClass());
        log.debug("[Remaining Listeners]:"+pointIdPointDataListeners.values());
        pointIdPointDataListeners.values().removeAll(ImmutableSet.of(l));
    }

    @Override
    public void registerForAllAlarms(SignalListener listener) {
        dispatchProxy.registerForAlarms();
        alarmSignalListeners.add(listener);
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

    private Set<LitePointData> getPointData(Set<Integer> pointIds) {
        return getPointData(pointIds, true);
    }

    @Override
    public Set<? extends PointValueQualityHolder> getPointDataOnce(Set<Integer> pointIds) {
        return getPointData(pointIds, false);
    }

    private Set<LitePointData> getPointData(Set<Integer> pointIds, boolean shouldRegister) {

        Function<Set<Integer>, Set<LitePointData>> getPointDataFromDispatch = 
                shouldRegister 
                    ? dispatchProxy::getPointData 
                    : dispatchProxy::getPointDataOnce;
        
        //  Try a cache lookup
        Map<Integer, LitePointData> pointData = Maps.newHashMap(Maps.asMap(pointIds, dynamicDataCache::getPointData));

        //  Create a stream containing the cached pointdata  
        Stream<LitePointData> cached = pointData.values().stream().filter(Objects::nonNull);
        
        //  Filter off the point IDs without data, partition them into chunks of 1000
        Iterable<List<Integer>> missingIds = Iterables.partition(Maps.filterValues(pointData, Objects::isNull).keySet(), 1000); 
        
        //  Create a stream that requests the non-cached IDs from Dispatch
        Stream<LitePointData> fromDispatch = 
                StreamUtils.stream(missingIds)
                    .map(HashSet::new)
                    .map(getPointDataFromDispatch)
                    .flatMap(Set::stream);
        
        //  Run through both the cached and non-cached streams, collecting into a single set
        return Stream.concat(cached, fromDispatch).collect(Collectors.toSet());
    }

    private LitePointData getPointData(int pointId) {
        LitePointData pointData = dynamicDataCache.getPointData(pointId);
        if (pointData == null) {
            pointData = dispatchProxy.getPointData(pointId);
        }
        return pointData;
    }
    
    @Override
    public void logListenerInfo(int pointId){
       Set<PointDataListener> listeners = pointIdPointDataListeners.get(pointId);
       log.info("Listeners listening for the point="+pointId);
       for (PointDataListener listener : listeners) {
           log.info("..."+listener);
       }
    }
}