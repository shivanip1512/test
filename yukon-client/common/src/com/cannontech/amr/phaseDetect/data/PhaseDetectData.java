package com.cannontech.amr.phaseDetect.data;

import java.util.List;

import com.cannontech.common.model.Route;

public class PhaseDetectData {
    
    private Integer intervalLength;
    private Integer deltaVoltage;
    private Integer numIntervals;
    private Integer substationId;
    private String substationName;
    private boolean readAfterAll;
    private List<Route> readRoutes;
    private List<Route> broadcastRoutes;
    
    public Integer getIntervalLength() {
        return intervalLength;
    }
    public void setIntervalLength(Integer intervalLength) {
        this.intervalLength = intervalLength;
    }
    public Integer getDeltaVoltage() {
        return deltaVoltage;
    }
    public void setDeltaVoltage(Integer deltaVoltage) {
        this.deltaVoltage = deltaVoltage;
    }
    public Integer getNumIntervals() {
        return numIntervals;
    }
    public void setNumIntervals(Integer numIntervals) {
        this.numIntervals = numIntervals;
    }
    public Integer getSubstationId() {
        return substationId;
    }
    public void setSubstationId(Integer substationId) {
        this.substationId = substationId;
    }
    public boolean isReadAfterAll() {
        return readAfterAll;
    }
    public void setReadAfterAll(boolean readAfterAll) {
        this.readAfterAll = readAfterAll;
    }
    
    public String getSubstationName() {
        return substationName;
    }
    
    public void setSubstationName(String substationName) {
        this.substationName = substationName;
    }
    
    public void setBroadcastRoutes(List<Route> broadcastRoutes) {
        this.broadcastRoutes = broadcastRoutes;
    }
    
    public List<Route> getBroadcastRoutes() {
        return broadcastRoutes;
    }
    
    public void setReadRoutes(List<Route> readRoutes) {
        this.readRoutes = readRoutes;
    }
    
    public List<Route> getReadRoutes() {
        return readRoutes;
    }
}