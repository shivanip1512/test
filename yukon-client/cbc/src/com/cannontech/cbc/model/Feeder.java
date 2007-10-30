package com.cannontech.cbc.model;

public class Feeder {

    private int currentVarLoadPointId;
    private int currentWattLoadPointId;
    private int id;
    private String mapLocationId;
    private int currentVoltLoadPointId;
    private char multiMonitorControl;
    private char usePhaseData;
    private int phaseb;
    private int phasec;
    
    public int getCurrentVarLoadPointId() {
        return currentVarLoadPointId;
    }
    public void setCurrentVarLoadPointId(int currentVarLoadPointId) {
        this.currentVarLoadPointId = currentVarLoadPointId;
    }
    public int getCurrentVoltLoadPointId() {
        return currentVoltLoadPointId;
    }
    public void setCurrentVoltLoadPointId(int currentVoltLoadPointId) {
        this.currentVoltLoadPointId = currentVoltLoadPointId;
    }
    public int getCurrentWattLoadPointId() {
        return currentWattLoadPointId;
    }
    public void setCurrentWattLoadPointId(int currentWattLoadPointId) {
        this.currentWattLoadPointId = currentWattLoadPointId;
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
    public char getMultiMonitorControl() {
        return multiMonitorControl;
    }
    public void setMultiMonitorControl(char multiMonitorControl) {
        this.multiMonitorControl = multiMonitorControl;
    }
    public int getPhaseb() {
        return phaseb;
    }
    public void setPhaseb(int phaseb) {
        this.phaseb = phaseb;
    }
    public int getPhasec() {
        return phasec;
    }
    public void setPhasec(int phasec) {
        this.phasec = phasec;
    }
    public char getUsePhaseData() {
        return usePhaseData;
    }
    public void setUsePhaseData(char usePhaseData) {
        this.usePhaseData = usePhaseData;
    }
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + currentVarLoadPointId;
        result = PRIME * result + currentVoltLoadPointId;
        result = PRIME * result + currentWattLoadPointId;
        result = PRIME * result + id;
        result = PRIME * result + ((mapLocationId == null) ? 0 : mapLocationId.hashCode());
        result = PRIME * result + multiMonitorControl;
        result = PRIME * result + phaseb;
        result = PRIME * result + phasec;
        result = PRIME * result + usePhaseData;
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
        final Feeder other = (Feeder) obj;
        if (currentVarLoadPointId != other.currentVarLoadPointId)
            return false;
        if (currentVoltLoadPointId != other.currentVoltLoadPointId)
            return false;
        if (currentWattLoadPointId != other.currentWattLoadPointId)
            return false;
        if (id != other.id)
            return false;
        if (mapLocationId == null) {
            if (other.mapLocationId != null)
                return false;
        } else if (!mapLocationId.equals(other.mapLocationId))
            return false;
        if (multiMonitorControl != other.multiMonitorControl)
            return false;
        if (phaseb != other.phaseb)
            return false;
        if (phasec != other.phasec)
            return false;
        if (usePhaseData != other.usePhaseData)
            return false;
        return true;
    }
    
    
}
