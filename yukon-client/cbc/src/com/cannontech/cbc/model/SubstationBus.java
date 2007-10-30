package com.cannontech.cbc.model;

public class SubstationBus {

    private int id;
    private int currentVarLoadPointId;
    private int currentWattLoadPointId;
    private String mapLocationId;
    private int currentVoltLoadPointId;
    private int altSubId;
    private int switchPointId;
    private char dualBusEnabled;
    private char multiMonitorControl;
    private char usephasedata;
    private int phaseb;
    private int phasec;
    
    public int getAltSubId() {
        return altSubId;
    }
    public void setAltSubId(int altSubId) {
        this.altSubId = altSubId;
    }
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
    public char getDualBusEnabled() {
        return dualBusEnabled;
    }
    public void setDualBusEnabled(char dualBusEnabled) {
        this.dualBusEnabled = dualBusEnabled;
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
    public int getSwitchPointId() {
        return switchPointId;
    }
    public void setSwitchPointId(int switchPointId) {
        this.switchPointId = switchPointId;
    }
    public char getUsephasedata() {
        return usephasedata;
    }
    public void setUsephasedata(char usephasedata) {
        this.usephasedata = usephasedata;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + altSubId;
        result = PRIME * result + currentVarLoadPointId;
        result = PRIME * result + currentVoltLoadPointId;
        result = PRIME * result + currentWattLoadPointId;
        result = PRIME * result + dualBusEnabled;
        result = PRIME * result + id;
        result = PRIME * result + ((mapLocationId == null) ? 0 : mapLocationId.hashCode());
        result = PRIME * result + multiMonitorControl;
        result = PRIME * result + phaseb;
        result = PRIME * result + phasec;
        result = PRIME * result + switchPointId;
        result = PRIME * result + usephasedata;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SubstationBus other = (SubstationBus) obj;
        if (altSubId != other.altSubId)
            return false;
        if (currentVarLoadPointId != other.currentVarLoadPointId)
            return false;
        if (currentVoltLoadPointId != other.currentVoltLoadPointId)
            return false;
        if (currentWattLoadPointId != other.currentWattLoadPointId)
            return false;
        if (dualBusEnabled != other.dualBusEnabled)
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
        if (switchPointId != other.switchPointId)
            return false;
        if (usephasedata != other.usephasedata)
            return false;
        return true;
    }

}
