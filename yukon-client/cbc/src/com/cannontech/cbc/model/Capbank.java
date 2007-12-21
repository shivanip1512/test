package com.cannontech.cbc.model;

import com.cannontech.capcontrol.CapBankOperationalState;

public class Capbank {
    private int id;
    private CapBankOperationalState operationalState;
    private String controllerType;
    private int controlDeviceId;
    private int controlPointId;
    private int bankSize;
    private String typeOfSwitch;
    private String switchManufacturer;
    private String mapLocationId;
    private int recloseDelay;
    private int maxDailyOps;
    private char maxOpDisable;
    
    public int getBankSize() {
        return bankSize;
    }
    public void setBankSize(int bankSize) {
        this.bankSize = bankSize;
    }
    public int getControlDeviceId() {
        return controlDeviceId;
    }
    public void setControlDeviceId(int controlDeviceId) {
        this.controlDeviceId = controlDeviceId;
    }
    public String getControllerType() {
        return controllerType;
    }
    public void setControllerType(String controllerType) {
        this.controllerType = controllerType;
    }
    public int getControlPointId() {
        return controlPointId;
    }
    public void setControlPointId(int controlPointId) {
        this.controlPointId = controlPointId;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getMapLocationId() {
        return mapLocationId;
    }
    public void setMapLocationId(String mapLocationId) {
        this.mapLocationId = mapLocationId;
    }
    public int getMaxDailyOps() {
        return maxDailyOps;
    }
    public void setMaxDailyOps(int maxDailyOps) {
        this.maxDailyOps = maxDailyOps;
    }
    public char getMaxOpDisable() {
        return maxOpDisable;
    }
    public void setMaxOpDisable(char maxOpDisable) {
        this.maxOpDisable = maxOpDisable;
    }
    public CapBankOperationalState getOperationalState() {
        return operationalState;
    }
    public void setOperationalState(CapBankOperationalState operationalState) {
        this.operationalState = operationalState;
    }
    public int getRecloseDelay() {
        return recloseDelay;
    }
    public void setRecloseDelay(int recloseDelay) {
        this.recloseDelay = recloseDelay;
    }
    public String getSwitchManufacturer() {
        return switchManufacturer;
    }
    public void setSwitchManufacturer(String switchManufacturer) {
        this.switchManufacturer = switchManufacturer;
    }
    public String getTypeOfSwitch() {
        return typeOfSwitch;
    }
    public void setTypeOfSwitch(String typeOfSwitch) {
        this.typeOfSwitch = typeOfSwitch;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + bankSize;
        result = prime * result + controlDeviceId;
        result = prime * result + controlPointId;
        result = prime * result + ((controllerType == null) ? 0
                : controllerType.hashCode());
        result = prime * result + id;
        result = prime * result + ((mapLocationId == null) ? 0
                : mapLocationId.hashCode());
        result = prime * result + maxDailyOps;
        result = prime * result + maxOpDisable;
        result = prime * result + ((operationalState == null) ? 0
                : operationalState.hashCode());
        result = prime * result + recloseDelay;
        result = prime * result + ((switchManufacturer == null) ? 0
                : switchManufacturer.hashCode());
        result = prime * result + ((typeOfSwitch == null) ? 0
                : typeOfSwitch.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Capbank other = (Capbank) obj;
        if (bankSize != other.bankSize)
            return false;
        if (controlDeviceId != other.controlDeviceId)
            return false;
        if (controlPointId != other.controlPointId)
            return false;
        if (controllerType == null) {
            if (other.controllerType != null)
                return false;
        } else if (!controllerType.equals(other.controllerType))
            return false;
        if (id != other.id)
            return false;
        if (mapLocationId == null) {
            if (other.mapLocationId != null)
                return false;
        } else if (!mapLocationId.equals(other.mapLocationId))
            return false;
        if (maxDailyOps != other.maxDailyOps)
            return false;
        if (maxOpDisable != other.maxOpDisable)
            return false;
        if (operationalState == null) {
            if (other.operationalState != null)
                return false;
        } else if (!operationalState.equals(other.operationalState))
            return false;
        if (recloseDelay != other.recloseDelay)
            return false;
        if (switchManufacturer == null) {
            if (other.switchManufacturer != null)
                return false;
        } else if (!switchManufacturer.equals(other.switchManufacturer))
            return false;
        if (typeOfSwitch == null) {
            if (other.typeOfSwitch != null)
                return false;
        } else if (!typeOfSwitch.equals(other.typeOfSwitch))
            return false;
        return true;
    }
    
}
