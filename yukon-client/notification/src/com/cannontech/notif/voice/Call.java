package com.cannontech.notif.voice;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.*;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.notif.outputs.Notification;

/**
 *  
 */
public class Call {
    private Logger log = YukonLogManager.getLogger(Call.class);

    private Notification message;
    private PhoneNumber number;
    static private AtomicInteger nextToken = new AtomicInteger(0);
    private final String token;
    private final int sequenceNumber;
    public static final String CALL_STATE = "state";
    private TreeMap<String, String> parameterMap;
    private final ContactPhone contactPhone;
    private volatile CountDownLatch connectionLatch;
    private volatile boolean retryPossible = true;
    private volatile boolean confirmationReceived = false;
    private List<Runnable> completionCallbacks = Collections.synchronizedList(new ArrayList<Runnable>());


    /**
     * @param contactPhone The phone number to be called
     * @param message The message to be delivered
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
        parameterMap.put("MESSAGE_TYPE", getMessage().getMessageType());

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

    public String toString() {
        return "Call #" + sequenceNumber + "(" + getToken() + ", " + number + ")";
    }

    public boolean equals(Object obj) {
        if (obj instanceof Call) {
            Call that = (Call) obj;
            return that.getToken().equals(getToken());
        }
        return false;
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

    public void newAttempt() {
        log.info("Call " + this + " received newAttempt");
        connectionLatch = new CountDownLatch(1);
        retryPossible = true;
        confirmationReceived = false;
    }

    public void handleCompletion() {
        log.info("Call " + this + " received completion");
        for (Runnable runner : completionCallbacks) {
            runner.run();
        }
    }
    
    public void addCompletionCallback(Runnable runner) {
        completionCallbacks.add(runner);
    }

    public void handleFailure(String why) {
        retryPossible = false;
        log.info("Call " + this + " received failure: " + why);
    }
    
    public void handleConfirmation() {
        log.info("Call " + this + " received confirmation");
        confirmationReceived = true;
    }

    public void handleDisconnect() {
        log.info("Call " + this + " received disconnect");
        retryPossible = false;
        connectionLatch.countDown();
    }
    
    public void handleConnectionFailed(String why) {
        log.info("Call " + this + " received connection failed: " + why);
        connectionLatch.countDown();
    }
    
    public boolean waitForLineToClear(int timeoutSeconds) {
        try {
            return connectionLatch.await(timeoutSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn("caught InterruptedException in waitForDisconnect", e);
        }
        return false;
    }

    public boolean isSuccess() {
        return confirmationReceived;
    }

    public boolean isRetry() {
        return retryPossible && !confirmationReceived;
    }

}
