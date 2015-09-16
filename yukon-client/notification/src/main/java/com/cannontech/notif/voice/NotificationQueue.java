package com.cannontech.notif.voice;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.UnknownRolePropertyException;
import com.cannontech.notif.outputs.UnknownCustomerException;
import com.cannontech.stars.energyCompany.model.EnergyCompany;


/**
 * A queue to manage pending SingleNotifications.
 */
public class NotificationQueue implements NotificationQueueMBean {
    private boolean _shutdown = false;
    private Map<Integer, CallPool> _poolMap = new TreeMap<>();
    private int _notificationsProcessed = 0;
    
    private @Autowired CallPoolFactory callPoolFactory;
    
    public NotificationQueue() {
        //MBeanUtil.tryRegisterMBean("name=NotificationCallQueue", this);
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
        if (!notification.getState().equals(SingleNotification.STATE_INITIAL)) {
            throw new UnsupportedOperationException("Can't add notification that isn't in ready state.");
        }
        try {
            
            final CallPool callPool = getCallPool(notification.getContactable().getEnergyCompany());
            
            notification.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getNewValue().equals(SingleNotification.STATE_READY)
                        && !_shutdown) {
                        try {
                            Call call = notification.createNewCall();
                            callPool.submitCall(call);
                            notification.setState(SingleNotification.STATE_CALLING);
                        } catch (NoRemainingCallsException e) {
                            notification.setState(SingleNotification.STATE_FAILED);
                        }
                    }
                }
            });
            CTILogger.info("Adding single notification: " + notification);
            notification.setState(SingleNotification.STATE_READY);
            _notificationsProcessed++;
        } catch (UnknownRolePropertyException e) {
            CTILogger.warn("Unable to add " + notification + " to calling queue.", e);
        }
    }
    
    public synchronized void shutdown() {
        _shutdown = true;
        for (Iterator<CallPool> iter = _poolMap.values().iterator(); iter.hasNext();) {
            CallPool pool = iter.next();
            pool.shutdown();
        }
    }
    
    private synchronized CallPool getCallPool(EnergyCompany energyCompany) throws UnknownRolePropertyException {
        if (_poolMap.containsKey(energyCompany.getId())) {
            return _poolMap.get(energyCompany.getId());
        }
        CallPool newPool = callPoolFactory.createCallPool(energyCompany);
        _poolMap.put(energyCompany.getId(), newPool);
        return newPool;
    }
    
    public synchronized Call getCall(String token) throws UnknownCallTokenException {
        for (Iterator<CallPool> iter = _poolMap.values().iterator(); iter.hasNext();) {
            CallPool pool = iter.next();
            Call call = pool.getCall(token);
            if (call != null) {
                return call;
            }
        }
        throw new UnknownCallTokenException(token);
    }
    
    @Override
    public int getActiveCalls() {
        int result = 0;
        for (Iterator<CallPool> iter = _poolMap.values().iterator(); iter.hasNext();) {
            CallPool pool = iter.next();
            result += pool.getNumberPendingCalls();
        }
        return result;
    }

    @Override
    public int getCallsProcessed() {
        return _notificationsProcessed;
    }

}
