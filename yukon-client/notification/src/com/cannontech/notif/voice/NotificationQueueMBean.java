package com.cannontech.notif.voice;

public interface NotificationQueueMBean {

    public int getActiveCalls();
    public int getCallsProcessed();
    public void setCallsProcessed(int callsProcessed);

}