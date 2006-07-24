package com.cannontech.core.dynamic.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointDataListener;
import com.cannontech.core.dynamic.SignalListener;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.cache.DBChangeLiteListener;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.PointRegistration;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.Command;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.message.util.ServerRequest;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.IServerConnection;

public class AsyncDynamicDataSourceImpl implements AsyncDynamicDataSource, MessageListener  {
    
    private IServerConnection dispatchConnection;
    private ServerRequest serverRequest;
    private IDatabaseCache databaseCache;
    
    private Map<Integer, LinkedHashSet<PointDataListener>> pointIdPointDataListeners =
        new HashMap<Integer, LinkedHashSet<PointDataListener>>();

    private Map<Integer, LinkedHashSet<SignalListener>> pointIdSignalListeners = 
            new HashMap<Integer, LinkedHashSet<SignalListener>>();
    
    private Map<PointDataListener, HashSet<Integer>> pointDataListenerPointIds =
            new HashMap<PointDataListener, HashSet<Integer>>();
    
    private Map<SignalListener, HashSet<Integer>> signalListenerPointIds = 
            new HashMap<SignalListener, HashSet<Integer>>();

    private Set<DBChangeListener> dbChangeListeners = 
        new HashSet<DBChangeListener>();
    
    private Set<DBChangeLiteListener> dbChangeLiteListeners = 
        new HashSet<DBChangeLiteListener>();

    public void registerForPointData(PointDataListener l, Set<Integer> pointIds) {

        //First register with dispatch point ids as necessary
        //If it throws then we won't have changed any of our state
        registerForPointIds(pointIds);
        
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

    public void unRegisterForPointData(PointDataListener l, Set<Integer> pointIds) {
        Set<Integer> listenerPointIds = pointDataListenerPointIds.get(l);        
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
        //Finally unregister with dispatch
        unregisterForPointIds(pointIds);
    }

    public void unRegisterForPointData(PointDataListener l) {
        Set<Integer> pointIds = new HashSet<Integer>(pointDataListenerPointIds.get(l));
        unRegisterForPointData(l, pointIds);
    }

    public void registerForSignals(SignalListener l, Set<Integer> pointIds) {

        //First register with dispatch point ids as necessary
        //If it throws then we won't have changed any of our state
        registerForPointIds(pointIds);
        
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

    public void unRegisterForSignals(SignalListener l, Set<Integer> pointIds) {
        Set<Integer> listenerPointIds = signalListenerPointIds.get(l);        
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
        //Finally unregister with dispatch
        unregisterForPointIds(pointIds);
    }

    public void unRegisterForSignals(SignalListener l) {
        Set<Integer> pointIds = new HashSet<Integer>(signalListenerPointIds.get(l));
        unRegisterForSignals(l, pointIds);
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
        if(o instanceof PointData) {
            handlePointData((PointData)o);
        }
        else if(o instanceof Signal) {
            handleSignal((Signal)o);
        }
        else if(o instanceof DBChangeMsg) {
            handleDBChange((DBChangeMsg)o);
        }
    }

    public void handlePointData(PointData pointData) {
        Set<PointDataListener> listeners = pointIdPointDataListeners.get(pointData.getId());
        for (PointDataListener listener : listeners) {
            listener.pointDataReceived(pointData);
        }
    }
    
    public void handleSignal(Signal signal) {
        Set<SignalListener> listeners = pointIdSignalListeners.get(signal.getPointID());
        for (SignalListener listener : listeners) {
            listener.signalReceived(signal);
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
    
    public void setDispatchConnection(IServerConnection dispatchConnection) {
        // Should we unregister with the old connection?? might leak otherwise?
        if(this.dispatchConnection != null) {
            this.dispatchConnection.removeMessageListener(this);
        }
        this.dispatchConnection = dispatchConnection;
        this.dispatchConnection.addMessageListener(this);
    }
    
    public void setServerRequest(ServerRequest serverRequest) {
        this.serverRequest = serverRequest;
    }
    
    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }
    
    /**
     * Given a set of point ids, return a sub set of those point ids that
     * we are currently not registered with dispatch for.
     * @param pointIds
     * @return
     */
    private Set<Integer> findUnregisteredPointIds(Set<Integer> pointIds) {
        Set<Integer> unregisteredPointIds = new HashSet<Integer>();
        for (Integer id : pointIds) {
            Set<PointDataListener> pdl = pointIdPointDataListeners.get(id);
            Set<SignalListener> sl = pointIdSignalListeners.get(id);
            if(pdl == null && sl == null) {
                unregisteredPointIds.add(id);
            }
        }
        return unregisteredPointIds;
    }
        
    private Set<Integer> findRegisteredPointIds(Set<Integer> pointIds) {
        Set<Integer> registeredPointIds = new HashSet<Integer>();
        for(Integer id : pointIds) {
            Set<PointDataListener> pdl = pointIdPointDataListeners.get(id);
            Set<SignalListener> sl = pointIdSignalListeners.get(id);
            if(pdl != null || sl != null) {
                registeredPointIds.add(id);
            }
        }
        return registeredPointIds;
    }
    
    private void registerForPointIds(Set<Integer> pointIds) throws DynamicDataAccessException {
        
        pointIds = findUnregisteredPointIds(pointIds);
        
        if(pointIds.size() > 0) {
            // Add these points to the current registration and then make a request for their current values
            PointRegistration pReg = new PointRegistration();
            pReg.setRegFlags(PointRegistration.REG_ADD_POINTS);
            pReg.setPointIds(pointIds);
            dispatchConnection.write(pReg);
            
            Command cmd = new Command();
            cmd.setOperation(Command.POINT_DATA_REQUEST);
            cmd.setOpArgList(new ArrayList<Integer>(pointIds));
            ServerResponseMsg resp = serverRequest.makeServerRequest(dispatchConnection, cmd);
            if(resp.getStatus() == ServerResponseMsg.STATUS_ERROR) {
                throw new DynamicDataAccessException(resp.getStatusStr());
            }
        }
    }
    
    /**
     * Unregister these points with dispatch so we stop getting point data messages.
     * @param dispatchConnection
     * @param pointIds
     */
    private void unregisterForPointIds(Set<Integer> pointIds) 
        throws DynamicDataAccessException {
        pointIds = findUnregisteredPointIds(pointIds);
        
        PointRegistration pReg = new PointRegistration();
        pReg.setRegFlags(PointRegistration.REG_REMOVE_POINTS);
        pReg.setPointIds(pointIds);
        dispatchConnection.write(pReg);
    }
    
    public Set<Integer> getPointIds(PointDataListener l) {
        Set<Integer> ids = pointDataListenerPointIds.get(l);
        if(ids == null) {
            return Collections.EMPTY_SET;
        }        
        return ids;
    }
    
    public Set<Integer> getPointIds(SignalListener l) {
        Set<Integer> ids = signalListenerPointIds.get(l);
        if(ids == null) {
            return Collections.EMPTY_SET;
        }
        return ids;
    }
    
    public Set<PointDataListener> getPointDataListeners(int pointId) {
        Set<PointDataListener> listeners = pointIdPointDataListeners.get(pointId);
        if(listeners == null) {
            return Collections.EMPTY_SET;
        }
        return listeners;
    }
    
    public Set<SignalListener> getSignalListeners(int pointId) {
        Set<SignalListener> listeners = pointIdSignalListeners.get(pointId);
        if(listeners == null) {
            return Collections.EMPTY_SET;
        }
        return listeners;
    }
    
}
