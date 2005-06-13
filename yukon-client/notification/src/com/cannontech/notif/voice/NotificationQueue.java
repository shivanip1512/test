package com.cannontech.notif.voice;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.cannontech.clientutils.CTILogger;


/**
 * A queue to manage pending SingleNotifications.
 */
public class NotificationQueue {
    private final CallPool _callPool;
    private boolean _shutdown = false;
    
    public NotificationQueue(CallPool callPool) {
        _callPool = callPool;
    }

    /**
     * Adds a SingleNotification object to the queue.
     * Once a SingleNotification object is added to the queue, it
     * will be managed internally to re-add it to the back of the queue
     * as necessary when one of the notification's calls fails.
     * Note: this is no longer actually implememnted with a queue,
     * but that's how it acts.
     * @param notification
     */
    public synchronized void add(final SingleNotification notification) {
        if (!notification.getState().equals(SingleNotification.STATE_READY)) {
            throw new UnsupportedOperationException("Can't add notification that isn't in ready state.");
        }
        notification.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue().equals(SingleNotification.STATE_READY)
                    && !_shutdown) {
                    Call call = notification.createNewCall();
                    _callPool.submitCall(call);
                }
            }
        });
        CTILogger.info("Adding single notification: " + notification);
        Call call = notification.createNewCall();
        _callPool.submitCall(call);
    }
    
    public void shutdown() {
        _shutdown = true;
    }

}
