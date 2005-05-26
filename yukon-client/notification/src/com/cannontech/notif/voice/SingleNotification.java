package com.cannontech.notif.voice;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.concurrent.PropertyChangeMulticaster;
import com.cannontech.notif.outputs.Callable;
import com.cannontech.notif.outputs.ContactPhone;
import com.cannontech.notif.voice.callstates.*;

/**
 * 
 */
public class SingleNotification implements PropertyChangeListener {
    public static final String STATE_COMPLETE = "complete";
    public static final String STATE_READY = "ready";
    public static final String STATE_CALLING = "calling";
    public static final String STATE_FAILED = "failed";
    public static final String NOTIFICATION_STATE = "state";
    
	PropertyChangeMulticaster _listeners = new PropertyChangeMulticaster(this);
	String _state = STATE_READY;
	private Iterator _phoneIterator;
	private Object _message;
    private Callable _customer;
	
	public SingleNotification(Callable customer, Object message) {
		_customer = customer;
        _phoneIterator = customer.getContactPhoneNumberList().iterator();
		_message = message;
	}
	
	public Call createNewCall() {
		ContactPhone contactPhone = (ContactPhone)_phoneIterator.next();
		Call nextCall = new Call(contactPhone, _message);
		nextCall.addPropertyChangeListener(this);
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
		_listeners.firePropertyChange(NOTIFICATION_STATE, oldState, state);
        CTILogger.info("Notification '" + this + "' changing from " + oldState + " to " + state);
        return oldState;
	}

	public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Call.CALL_STATE)) {
            CallState callState = (CallState) evt.getNewValue();
            if (callState instanceof Confirmed) {
                setState(STATE_COMPLETE);
            } else if (callState instanceof Connecting) {
                setState(STATE_CALLING);
            } else if (callState.isDone()) {
                if (_phoneIterator.hasNext()) {
                    setState(STATE_READY);
                } else {
                    setState(STATE_FAILED);
                }
            }
        }
    }
        
    public Object getMessage() {
        return _message;
    }
    public String toString() {
        return "Message " + getMessage() + " for " + _customer;
    }
}
