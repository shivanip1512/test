package com.cannontech.notif.voice;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;

import com.cannontech.clientutils.CTILogger;

import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;


/**
 * A queue to manage pending SingleNotifications.
 */
public class NotificationQueue {
    LinkedBlockingQueue _readyQueue = new LinkedBlockingQueue();

    HashSet _pendingNotifications = new HashSet();

    /**
     * Adds a SingleNotification object to the queue.
     * Once a SingleNotification object is added to the queue, it
     * will be managed internally to re-add it to the back of the queue
     * as necessary when one of the notification's calls fails.
     * @param notification
     */
    public synchronized void add(final SingleNotification notification) {
        if (!notification.getState().equals(SingleNotification.STATE_READY)) {
            throw new UnsupportedOperationException("Can't add notification that isn't in ready state.");
        }
        if (_pendingNotifications.contains(notification)) {
            throw new UnsupportedOperationException("Notification is already registered.");
        }
        notification.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue().equals(SingleNotification.STATE_COMPLETE)) {
                    _pendingNotifications.remove(evt.getSource());
                } else if (evt.getNewValue().equals(SingleNotification.STATE_FAILED)) {
                    _pendingNotifications.remove(evt.getSource());
                } else if (evt.getNewValue().equals(SingleNotification.STATE_READY)) {
                    try {
                        _readyQueue.put(evt.getSource());
                    } catch (InterruptedException e) {
                        handleInterruptedException(notification, e);
                    }
                }
            }
        });

        // add to list
        try {
            // The documentation says this throws an InterruptedException.
            // However, because this is an unbounded queue, this call just
            // isn't going to block for any significant amount of time. So, 
            // I don't think it is very likely that it ever will get thrown 
            // (plus, I don't believe the caller ever gets its thread 
            // interrupted) and that's what justifies the lame handling.
            _readyQueue.put(notification);
            _pendingNotifications.add(notification);
            CTILogger.info("Notification added to queue: " + notification);
        } catch (InterruptedException e) {
            handleInterruptedException(notification, e);
        }
    }

    public SingleNotification getNext() throws InterruptedException {
        return (SingleNotification) _readyQueue.take();
    }
    
    private void handleInterruptedException(SingleNotification notification, InterruptedException e) {
        // I don't know why we'd get inturrupted, or what to do
        // but this will let someone else deal with it!
        CTILogger.error("Thread was unexpectedly interrupted, " +
                        "eating exception and resseting interrupt." +
                        "Dropping notification of: " + notification, e);
        notification.setState(SingleNotification.STATE_FAILED);
        Thread.currentThread().interrupt();
        
    }

}
