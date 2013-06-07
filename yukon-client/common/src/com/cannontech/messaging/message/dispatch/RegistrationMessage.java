package com.cannontech.messaging.message.dispatch;

import com.cannontech.messaging.message.BaseMessage;

public class RegistrationMessage extends BaseMessage {

    private String appName;
    private int appIsUnique;
    private int appKnownPort;
    private int appExpirationDelay;

    private long regId = System.currentTimeMillis();

    public long getRegId() {
        return regId;
    }

    public int getAppExpirationDelay() {
        return appExpirationDelay;
    }

    public int getAppIsUnique() {
        return appIsUnique;
    }

    public int getAppKnownPort() {
        return appKnownPort;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppExpirationDelay(int newValue) {
        this.appExpirationDelay = newValue;
    }

    public void setAppIsUnique(int newValue) {
        this.appIsUnique = newValue;
    }

    public void setAppKnownPort(int newValue) {
        this.appKnownPort = newValue;
    }

    public void setAppName(String newValue) {
        this.appName = newValue;
    }
}
