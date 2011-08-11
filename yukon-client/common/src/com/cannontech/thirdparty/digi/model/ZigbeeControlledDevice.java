package com.cannontech.thirdparty.digi.model;

import org.joda.time.Instant;

public class ZigbeeControlledDevice {
    private int eventId;
    private String loadGroupName;
    private String deviceName;
    private boolean ack;
    private Instant start;
    private Instant stop;
    private Boolean canceled;
    
    public ZigbeeControlledDevice() {
        
    }

    public int getEventId() {
        return eventId;
    }
    
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
    
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String name) {
        this.deviceName = name;
    }

    public String getLoadGroupName() {
        return loadGroupName;
    }
    
    public void setLoadGroupName(String name) {
        this.loadGroupName = name;
    }
    
    public boolean isAck() {
        return ack;
    }

    public void setAck(boolean ack) {
        this.ack = ack;
    }

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getStop() {
        return stop;
    }

    public void setStop(Instant stop) {
        this.stop = stop;
    }

    public Boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }
}
