package com.cannontech.notif.voice;

import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.concurrent.PropertyChangeMulticaster;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.NotificationTypeChecker;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.notif.outputs.Contactable;
import com.cannontech.notif.outputs.Notification;


/**
 * 
 */
public class SingleNotification {
    private Logger log = YukonLogManager.getLogger(SingleNotification.class);
    
    public static final String STATE_COMPLETE = "Complete";
    public static final String STATE_READY = "Ready";
    public static final String STATE_INITIAL = "Initial";
    public static final String STATE_CALLING = "Calling";
    public static final String STATE_FAILED = "Failed";
    public static final String NOTIFICATION_STATE = "notifstate";
    
    static public final NotificationTypeChecker checker = new NotificationTypeChecker() {
        @Override
        public boolean validNotifcationType(ContactNotificationType notificationType) {
            return notificationType.isPhoneType();
        };
    };
        
	PropertyChangeMulticaster _listeners = new PropertyChangeMulticaster(this);
	String _state = STATE_INITIAL;
	private Iterator<LiteContactNotification> _phoneIterator;
	private Notification _message;
    private Contactable _contactable;
    private Call nextCall;
    private String _token;
    private NotificationStatusLogger _notificationLogger;
    static private AtomicInteger _nextToken = new AtomicInteger(0);

	
	public SingleNotification(Contactable contactable, Notification message) {
		_contactable = contactable;
        _phoneIterator = contactable.getNotifications(checker).iterator();
		_message = message;
        _token = "NOTIF-" + _nextToken.incrementAndGet();

	}
	
	public Call createNewCall() throws NoRemainingCallsException {
	    final LiteContactNotification contactNotif;
        synchronized (_phoneIterator) {
            if (!_phoneIterator.hasNext()) {
                throw new NoRemainingCallsException();
            }
            contactNotif = _phoneIterator.next();
        }
        PhoneNumber phoneNumber = new PhoneNumber(contactNotif.getNotification());
        ContactPhone contactPhone = new ContactPhone(phoneNumber, contactNotif.getContactID());
		nextCall = new Call(contactPhone, _message);
		
		final Call call = nextCall;
		nextCall.addCompletionCallback(new Runnable() {
            @Override
            public void run() {
                if (call.isSuccess()) {
                    _notificationLogger.logIndividualNotification(contactNotif, true);
                    setState(STATE_COMPLETE);
                } else {
                    _notificationLogger.logIndividualNotification(contactNotif, false);
                    setState(STATE_READY);
                }
            }
        });
		
		log.info("Created " + nextCall + " for " + this);
        return nextCall;
	}
	
	/**
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		_listeners.addPropertyChangeListener(listener);
	}
	/**
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		_listeners.removePropertyChangeListener(listener);
	}
	/**
	 * @return Returns the state.
	 */
	public synchronized String getState() {
		return _state;
	}
	
	protected synchronized String assignState(String newState) {
		String oldState = _state;
		_state = newState;
		return oldState;
	}
	
	/**
	 * @param state The state to set.
	 */
	public String setState(String state) {
		String oldState = assignState(state);
		log.info(this + " changing state " + oldState + " -> " + state);
        if (!oldState.equals(state)) {
    		_listeners.firePropertyChange(NOTIFICATION_STATE, oldState, state);
        }
        return oldState;
	}

    public Notification getMessage() {
        return _message;
    }
    
    @Override
    public String toString() {
        return _token + " (" + _contactable + ")";
    }
    
    public Call getLastCall() {
        return nextCall;
    }

    public Contactable getContactable() {
        return _contactable;
    }

    public void setNotificationLogger(NotificationStatusLogger notificationLogger) {
        _notificationLogger = notificationLogger;
    }
}
