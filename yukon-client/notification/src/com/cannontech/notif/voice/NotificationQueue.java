package com.cannontech.notif.voice;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.notif.outputs.UnknownCustomerException;
import com.cannontech.roles.ivr.OutboundCallingRole;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.roles.yukon.VoiceServerRole;


/**
 * A queue to manage pending SingleNotifications.
 */
public class NotificationQueue {
    private boolean _shutdown = false;
    private Map _poolMap = new TreeMap();
    
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
    }
    
    public synchronized void shutdown() {
        _shutdown = true;
        for (Iterator iter = _poolMap.values().iterator(); iter.hasNext();) {
            CallPool pool = (CallPool) iter.next();
            pool.shutdown();
        }
    }
    
    private synchronized CallPool getCallPool(LiteEnergyCompany energyCompany) {
        if (_poolMap.containsKey(energyCompany)) {
            return (CallPool) _poolMap.get(energyCompany);
        } else {
            CallPool newPool = createCallPool(energyCompany);
            _poolMap.put(energyCompany, newPool);
            return newPool;
        }
    }
    
    private CallPool createCallPool(LiteEnergyCompany energyCompany) {
        String voiceHost = RoleFuncs.getGlobalPropertyValue(SystemRole.VOICE_HOST);
        String voiceApp = RoleFuncs.getGlobalPropertyValue(OutboundCallingRole.VOICE_APP);
        VocomoDialer dialer = new VocomoDialer(voiceHost, voiceApp);
        dialer.setPhonePrefix(RoleFuncs.getGlobalPropertyValue(VoiceServerRole.CALL_PREFIX));
        dialer.setCallTimeout(Integer.parseInt(RoleFuncs.getGlobalPropertyValue(VoiceServerRole.CALL_TIMEOUT)));
        
        int callTimeout = Integer.parseInt(RoleFuncs.getGlobalPropertyValue(VoiceServerRole.CALL_RESPONSE_TIMEOUT));
        int numberOfChannels = Integer.parseInt(RoleFuncs.getGlobalPropertyValue(OutboundCallingRole.NUMBER_OF_CHANNELS));
        return new CallPool(dialer, numberOfChannels, callTimeout);

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

}
