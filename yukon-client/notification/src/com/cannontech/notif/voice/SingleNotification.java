package com.cannontech.notif.voice;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.concurrent.PropertyChangeMulticaster;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.notif.outputs.ContactPhone;
import com.cannontech.notif.outputs.Contactable;
import com.cannontech.notif.voice.callstates.*;
import com.cannontech.user.UserUtils;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 */
public class SingleNotification implements PropertyChangeListener {
    public static final String STATE_COMPLETE = "Complete";
    public static final String STATE_READY = "Ready";
    public static final String STATE_INITIAL = "Initial";
    public static final String STATE_CALLING = "Calling";
    public static final String STATE_FAILED = "Failed";
    public static final String NOTIFICATION_STATE = "notifstate";
    
    public static final Set VOICE_NOTIFICATION_TYPES = new HashSet(3);
    static {
        VOICE_NOTIFICATION_TYPES.add(new Integer(YukonListEntryTypes.YUK_ENTRY_ID_PHONE));
        VOICE_NOTIFICATION_TYPES.add(new Integer(YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE));
        VOICE_NOTIFICATION_TYPES.add(new Integer(YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE));
    }
    
	PropertyChangeMulticaster _listeners = new PropertyChangeMulticaster(this);
	String _state = STATE_INITIAL;
	private Iterator _phoneIterator;
	private Object _message;
    private Contactable _contactable;
    private Call _nextCall;
    private String _token;
    static private AtomicInteger _nextToken = new AtomicInteger(0);

	
	public SingleNotification(Contactable contactable, Object message) {
		_contactable = contactable;
        _phoneIterator = contactable.getNotifications(VOICE_NOTIFICATION_TYPES).iterator();
		_message = message;
        _token = "NOTIF-" + _nextToken.incrementAndGet();

	}
	
	public Call createNewCall() throws NoRemainingCallsException {
	    LiteContactNotification contactNotif;
        synchronized (_phoneIterator) {
            if (!_phoneIterator.hasNext()) {
                throw new NoRemainingCallsException();
            }
            contactNotif = (LiteContactNotification)_phoneIterator.next();
        }
        LiteContact contact = ContactFuncs.getContact(contactNotif.getContactID());
        if (contact.getLoginID() == UserUtils.USER_DEFAULT_ID) {
            CTILogger.warn("Unable to contact " + contactNotif + " of " + _contactable + " because there is no associated YukonUser.");
            return createNewCall();
        } else if (!ContactFuncs.hasPin(contact.getContactID())){
            CTILogger.warn("Unable to contact " + contactNotif + " of " + _contactable + " because there is no associated PIN.");
            return createNewCall();
        } else {
            PhoneNumber phoneNumber = new PhoneNumber(contactNotif.getNotification());
            ContactPhone contactPhone = new ContactPhone(phoneNumber, contactNotif.getContactID());
    		_nextCall = new Call(contactPhone, _message);
            _nextCall.addPropertyChangeListener(this);
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
}
