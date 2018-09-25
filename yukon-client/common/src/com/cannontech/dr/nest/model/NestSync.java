package com.cannontech.dr.nest.model;

import java.util.List;

import org.joda.time.Instant;

public class NestSync {
    private int id;
    private Instant startTime;
    private Instant stopTime;
    private List<NestSyncDetail> details;
    
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
    public List<NestSyncDetail> getDetails() {
        return details;
    }
    public void setDetails(List<NestSyncDetail> details) {
        this.details = details;
    }
}
