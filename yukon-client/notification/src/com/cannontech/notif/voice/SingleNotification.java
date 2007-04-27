package com.cannontech.notif.voice;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.concurrent.PropertyChangeMulticaster;
import com.cannontech.common.util.NotificationTypeChecker;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.notif.outputs.Contactable;
import com.cannontech.notif.voice.callstates.*;
import com.cannontech.user.UserUtils;


/**
 * 
 */
public class SingleNotification {
    public static final String STATE_COMPLETE = "Complete";
    public static final String STATE_READY = "Ready";
    public static final String STATE_INITIAL = "Initial";
    public static final String STATE_CALLING = "Calling";
    public static final String STATE_FAILED = "Failed";
    public static final String NOTIFICATION_STATE = "notifstate";
    
    static public final NotificationTypeChecker checker = new NotificationTypeChecker() {
        public boolean validNotifcationType(int notificationCategoryId) {
            return DaoFactory.getYukonListDao().isPhoneNumber(notificationCategoryId);
        };
    };
        
	PropertyChangeMulticaster _listeners = new PropertyChangeMulticaster(this);
	String _state = STATE_INITIAL;
	private Iterator _phoneIterator;
	private Object _message;
    private Contactable _contactable;
    private Call _nextCall;
    private String _token;
    private NotificationStatusLogger _notificationLogger;
    static private AtomicInteger _nextToken = new AtomicInteger(0);

	
	public SingleNotification(Contactable contactable, Object message) {
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
            contactNotif = (LiteContactNotification)_phoneIterator.next();
        }
        LiteContact contact = DaoFactory.getContactDao().getContact(contactNotif.getContactID());
        if (contact.getLoginID() == UserUtils.USER_DEFAULT_ID) {
            CTILogger.warn("Unable to contact " + contactNotif + " of " + _contactable + " because there is no associated YukonUser.");
            return createNewCall();
        } else if (!DaoFactory.getContactDao().hasPin(contact.getContactID())){
            CTILogger.warn("Unable to contact " + contactNotif + " of " + _contactable + " because there is no associated PIN.");
            return createNewCall();
        } else {
            PhoneNumber phoneNumber = new PhoneNumber(contactNotif.getNotification());
            ContactPhone contactPhone = new ContactPhone(phoneNumber, contactNotif.getContactID());
    		_nextCall = new Call(contactPhone, _message);
            _nextCall.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    // check for changes to a call's state
                    if (evt.getPropertyName().equals(Call.CALL_STATE)) {
                        CallState callState = (CallState) evt.getNewValue();
                        if (callState instanceof Confirmed) {
                            _notificationLogger.logIndividualNotification(contactNotif, true);
                            setState(STATE_COMPLETE);
                        } else if (callState instanceof Connecting) {
                            setState(STATE_CALLING);
                        } else if (callState.isDone()) {
                            _notificationLogger.logIndividualNotification(contactNotif, false);
                            setState(STATE_READY);
                        }
                    }
                }
            });
            CTILogger.info("Created " + _nextCall + " for " + this);
            return _nextCall;
        }
	}
	
    public void propertyChange(PropertyChangeEvent evt) {
        // check for changes to a call's state
        if (evt.getPropertyName().equals(Call.CALL_STATE)) {
            CallState callState = (CallState) evt.getNewValue();
            if (callState instanceof Confirmed) {
                setState(STATE_COMPLETE);
            } else if (callState instanceof Connecting) {
                setState(STATE_CALLING);
            } else if (callState.isDone()) {
                setState(STATE_READY);
            }
        }
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
		CTILogger.info(this + " changing state " + oldState + " -> " + state);
        if (!oldState.equals(state)) {
    		_listeners.firePropertyChange(NOTIFICATION_STATE, oldState, state);
        }
        return oldState;
	}

    public Object getMessage() {
        return _message;
    }
    
    public String toString() {
        return _token + " (" + _contactable + ")";
    }
    
    public Call getLastCall() {
        return _nextCall;
    }

    public Contactable getContactable() {
        return _contactable;
    }

    public void setNotificationLogger(NotificationStatusLogger notificationLogger) {
        _notificationLogger = notificationLogger;
    }
}
