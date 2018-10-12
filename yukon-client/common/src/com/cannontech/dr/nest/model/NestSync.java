package com.cannontech.dr.nest.model;

import org.joda.time.Instant;

public class NestSync {
    private int id;
    private Instant startTime = new Instant();
    private Instant stopTime;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Instant getStartTime() {
        return startTime;
    }
    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }
    public Instant getStopTime() {
        return stopTime;
    }
    public void setStopTime(Instant stopTime) {
        this.stopTime = stopTime;
    }
}
