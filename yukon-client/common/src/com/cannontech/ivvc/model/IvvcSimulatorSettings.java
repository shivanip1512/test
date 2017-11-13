package com.cannontech.ivvc.model;

import java.io.Serializable;

public class IvvcSimulatorSettings implements Serializable {

    private boolean increasedSpeedMode = false;
    private boolean autoGenerateSubstationBuskWh = true;
    private double substationBuskWh = 3000;
    private double localVoltageOffsetVar = 1200;
    private double remoteVoltageOffsetVar = 1200;
    private String blockedPoints = "";
    private String badQualityPoints = "";
    
    public IvvcSimulatorSettings() {
    }

    public IvvcSimulatorSettings(boolean increasedSpeedMode, double substationBuskWh,
            boolean autoGenerateSubstationBuskWh, double localVoltageOffsetVar, double remoteVoltageOffsetVar) {
        this.increasedSpeedMode = increasedSpeedMode;
        this.substationBuskWh = substationBuskWh;
        this.autoGenerateSubstationBuskWh = autoGenerateSubstationBuskWh;
        this.localVoltageOffsetVar = localVoltageOffsetVar;
        this.remoteVoltageOffsetVar = remoteVoltageOffsetVar;
    }

    public boolean isIncreasedSpeedMode() {
        return increasedSpeedMode;
    }

    public void setIncreasedSpeedMode(boolean increasedSpeedMode) {
        this.increasedSpeedMode = increasedSpeedMode;
    }

    public double getSubstationBuskWh() {
        return substationBuskWh;
    }

    public void setSubstationBuskWh(double substationBuskWh) {
        this.substationBuskWh = substationBuskWh;
    }
    
    public boolean isAutoGenerateSubstationBuskWh() {
        return autoGenerateSubstationBuskWh;
    }

    public void setAutoGenerateSubstationBuskWh(boolean autoGenerateSubstationBuskWh) {
        this.autoGenerateSubstationBuskWh = autoGenerateSubstationBuskWh;
    }

    public double getLocalVoltageOffsetVar() {
        return localVoltageOffsetVar;
    }

    public void setLocalVoltageOffsetVar(double localVoltageOffset) {
        this.localVoltageOffsetVar = localVoltageOffset;
    }

    public double getRemoteVoltageOffsetVar() {
        return remoteVoltageOffsetVar;
    }

    public void setRemoteVoltageOffsetVar(double remoteVoltageOffset) {
        this.remoteVoltageOffsetVar = remoteVoltageOffset;
    }

    public String getBlockedPoints() {
        return blockedPoints;
    }

    public void setBlockedPoints(String blockedPoints) {
        this.blockedPoints = blockedPoints;
    }

    public String getBadQualityPoints() {
        return badQualityPoints;
    }

    public void setBadQualityPoints(String badQualityPoints) {
        this.badQualityPoints = badQualityPoints;
    }    
}
