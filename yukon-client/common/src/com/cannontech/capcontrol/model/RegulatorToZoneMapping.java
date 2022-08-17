package com.cannontech.capcontrol.model;

import com.cannontech.common.model.Phase;

public class RegulatorToZoneMapping {
    private int regulatorId;
    private int zoneId;
    private double graphStartPosition;
    private Phase phase;
    
    public RegulatorToZoneMapping() {}
    
    public RegulatorToZoneMapping(Phase phase) {
        this.phase = phase;
    }

    public int getRegulatorId() {
        return regulatorId;
    }

    public void setRegulatorId(int regulatorId) {
        this.regulatorId = regulatorId;
    }

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    public double getGraphStartPosition() {
        return graphStartPosition;
    }

    public void setGraphStartPosition(double graphStartPosition) {
        this.graphStartPosition = graphStartPosition;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

}
