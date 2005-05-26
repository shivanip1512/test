package com.cannontech.notif.voice;

import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.TreeMap;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.concurrent.PropertyChangeMulticaster;
import com.cannontech.notif.outputs.ContactPhone;
import com.cannontech.notif.voice.callstates.*;

/**
 *  
 */
public class Call {
    CallState _state = new Pending();
    PropertyChangeMulticaster _listeners = new PropertyChangeMulticaster(this);
    private Object _message;
    private PhoneNumber _number;
    static private int nextToken = 1;
    final String _token;
    public static final String CALL_STATE = "state";
    private TreeMap _parameterMap;
    private final ContactPhone _contactPhone;

    /**
     * @param contactPhone
     *            The phone number to be called
     * @param message
     *            The message to be delivered
     */
    protected Call(ContactPhone contactPhone, Object message) {
        _contactPhone = contactPhone;
        _number = contactPhone.getPhoneNumber();
        
        _message = message;
        _token = "CALLID" + nextToken++;

        _parameterMap = new TreeMap();
        _parameterMap.put("token", _token);
        _parameterMap.put("contactid", new Integer(getContactId()));
    }

    /**
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        _listeners.addPropertyChangeListenerIfAbsent(listener);
    }

    /**
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        _listeners.removePropertyChangeListener(listener);
    }
    
    public void changeState(CallState newState) {
        CallState oldState;
        synchronized (this) {
            oldState = _state;
            _state = newState;
        }
        CTILogger.info("Call " + this + " changing state " + oldState + " -> " + newState);
        _listeners.firePropertyChange(CALL_STATE, oldState, newState);
    }
    
    public String getToken() {
        return _token;
    }
    
    public PhoneNumber getNumber() {
        return _number;
    }
    
    public Object getMessage() {
        return _message;
    }
    
    public CallState getState() {
        return _state;
    }
    
    public boolean isRetry() {
        return _state instanceof Retry;
    }
    
    public boolean isDone() {
        return _state.isDone();
    }
    
    public boolean isReady() {
        return _state.isReady();
    }
    
    public String toString() {
        return getToken() + " (" + _number + ")";
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof Call) {
            Call that = (Call)obj;
            return that.getNumber().equals(getNumber()) 
              && that.getMessage().equals(getMessage());
        }
        return false;
    }

    public synchronized void handleTimeout() {
        _state.handleTimeout(this);
    }
    
    public int getContactId() {
        return _contactPhone.getContactId();
    }
    
    public Map getCallParameters() {
        return _parameterMap;
    }

}
