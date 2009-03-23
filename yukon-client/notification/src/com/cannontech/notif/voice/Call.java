package com.cannontech.notif.voice;

import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.RandomStringUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.concurrent.PropertyChangeMulticaster;
import com.cannontech.notif.outputs.Notification;
import com.cannontech.notif.voice.callstates.*;


/**
 *  
 */
public class Call {
    private CallState state = new Pending();
    private PropertyChangeMulticaster listeners = new PropertyChangeMulticaster(this);
    private Notification message;
    private PhoneNumber number;
    static private AtomicInteger nextToken = new AtomicInteger(0);
    private final String token;
    private final int sequenceNumber;
    public static final String CALL_STATE = "state";
    private TreeMap<String, String> parameterMap;
    private final ContactPhone contactPhone;

    /**
     * @param contactPhone
     *            The phone number to be called
     * @param message
     *            The message to be delivered
     */
    public Call(ContactPhone contactPhone, Notification message) {
        this.contactPhone = contactPhone;
        number = contactPhone.getPhoneNumber();
        
        this.message = message;
        token = "CALL-" + RandomStringUtils.randomAlphanumeric(12);
        sequenceNumber = nextToken.incrementAndGet();

        parameterMap = new TreeMap<String, String>();
        parameterMap.put("TOKEN", token);
        parameterMap.put("CONTACTID", Integer.toString(getContactId()));
        parameterMap.put("CALL_STATE", getState().toString());
        parameterMap.put("MESSAGE_TYPE", getMessage().getMessageType());

    }

    /**
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.addPropertyChangeListenerIfAbsent(listener);
    }

    /**
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.removePropertyChangeListener(listener);
    }
    
    public void changeState(CallState newState) {
        CallState oldState;
        synchronized (this) {
            oldState = state;
            state = newState;
        }
        CTILogger.info(this + " changing state " + oldState + " -> " + newState);
        listeners.firePropertyChange(CALL_STATE, oldState, newState);
    }
    
    public String getToken() {
        return token;
    }
    
    public PhoneNumber getNumber() {
        return number;
    }
    
    public Notification getMessage() {
        return message;
    }
    
    public CallState getState() {
        return state;
    }
    
    public boolean isRetry() {
        return state instanceof Retry;
    }
    
    public boolean isDone() {
        return state.isDone();
    }
    
    public boolean isReady() {
        return state.isReady();
    }
    
    public String toString() {
        return "Call #" + sequenceNumber + "(" + getToken() + ", " + number + ")";
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof Call) {
            Call that = (Call)obj;
            return that.getToken().equals(getToken());
        }
        return false;
    }

    public synchronized void handleTimeout() {
        state.handleTimeout(this);
    }
    
    public int getContactId() {
        return this.contactPhone.getContactId();
    }
    
    public Map<String, String> getCallParameters() {
        return parameterMap;
    }
    
    public int hashCode() {
        return token.hashCode();
    }

}
