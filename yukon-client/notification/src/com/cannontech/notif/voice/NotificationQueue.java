package com.cannontech.notif.voice;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.functions.UnknownRolePropertyException;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.notif.outputs.UnknownCustomerException;


/**
 * A queue to manage pending SingleNotifications.
 */
public class NotificationQueue implements NotificationQueueMBean {
    private boolean _shutdown = false;
    private Map _poolMap = new TreeMap();
    private int _callsProcessed = 0;
    
    public NotificationQueue() {
    }

    /**
     * Adds a SingleNotification object to the queue.
     * Once a SingleNotification object is added to the queue, it
     * will be managed internally to re-add it to the back of the queue
     * as necessary when one of the notification's calls fails.
     * Note: this is no longer actually implememnted with a queue,
     * but that's how it acts.
     * @param notification
     * @throws UnknownCustomerException 
     */
    public void add(final SingleNotification notification) {
        if (!notification.getState().equals(SingleNotification.STATE_READY)) {
            throw new UnsupportedOperationException("Can't add notification that isn't in ready state.");
        }
        try {
            
            final CallPool callPool = getCallPool(notification.getContactable().getEnergyCompany());
            
            notification.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getNewValue().equals(SingleNotification.STATE_READY)
                        && !_shutdown) {
                        Call call = notification.createNewCall();
                        callPool.submitCall(call);
                    }
                }
            });
            CTILogger.info("Adding single notification: " + notification);
            Call call = notification.createNewCall();
            callPool.submitCall(call);
            _callsProcessed++;
        } catch (UnknownRolePropertyException e) {
            CTILogger.warn("Unable to add " + notification + " to calling queue.", e);
        }
    }
    
    public synchronized void shutdown() {
        _shutdown = true;
        for (Iterator iter = _poolMap.values().iterator(); iter.hasNext();) {
            CallPool pool = (CallPool) iter.next();
            pool.shutdown();
        }
    }
    
    private synchronized CallPool getCallPool(LiteEnergyCompany energyCompany) throws UnknownRolePropertyException {
        if (_poolMap.containsKey(energyCompany)) {
            return (CallPool) _poolMap.get(energyCompany);
        } else {
            CallPool newPool = new CallPool(energyCompany);
            _poolMap.put(energyCompany, newPool);
            return newPool;
        }
    }
    
    public synchronized Call getCall(String token) throws UnknownCallTokenException {
        for (Iterator iter = _poolMap.values().iterator(); iter.hasNext();) {
            CallPool pool = (CallPool) iter.next();
            Call call = pool.getCall(token);
            if (call != null) {
                return call;
            }
        }
        throw new UnknownCallTokenException(token);
    }
    
    public int getActiveCalls() {
        int result = 0;
        for (Iterator iter = _poolMap.values().iterator(); iter.hasNext();) {
            CallPool pool = (CallPool) iter.next();
            result += pool.getNumberPendingCalls();
        }
        return result;
    }

    public int getCallsProcessed() {
        return _callsProcessed;
    }

    public void setCallsProcessed(int callsProcessed) {
        _callsProcessed = callsProcessed;
    }

}
