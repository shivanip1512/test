package com.cannontech.cbc.model;

import com.cannontech.common.util.CtiUtilities;

public class Feeder {
	
	private String name;
	private int id;
	private int currentVarLoadPointId = CtiUtilities.NONE_ZERO_ID;
    private int currentWattLoadPointId = CtiUtilities.NONE_ZERO_ID;
    private String mapLocationId = "0";
    private int currentVoltLoadPointId = CtiUtilities.NONE_ZERO_ID;
    private String multiMonitorControl = "N";
    private String usePhaseData = "N";
    private int phaseb = CtiUtilities.NONE_ZERO_ID;
    private int phasec = CtiUtilities.NONE_ZERO_ID;
    private String controlFlag = "N";
    
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
    public String getMultiMonitorControl() {
        return multiMonitorControl;
    }
    public void setMultiMonitorControl(String multiMonitorControl) {
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
    public String getUsePhaseData() {
        return usePhaseData;
    }
    public void setUsePhaseData(String usePhaseData) {
        this.usePhaseData = usePhaseData;
    }
    public String getControlFlag() {
		return controlFlag;
	}
	public void setControlFlag(String controlFlag) {
		this.controlFlag = controlFlag;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((controlFlag == null) ? 0 : controlFlag.hashCode());
		result = prime * result + currentVarLoadPointId;
		result = prime * result + currentVoltLoadPointId;
		result = prime * result + currentWattLoadPointId;
		result = prime * result + id;
		result = prime * result
				+ ((mapLocationId == null) ? 0 : mapLocationId.hashCode());
		result = prime
				* result
				+ ((multiMonitorControl == null) ? 0 : multiMonitorControl
						.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + phaseb;
		result = prime * result + phasec;
		result = prime * result
				+ ((usePhaseData == null) ? 0 : usePhaseData.hashCode());
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
		Feeder other = (Feeder) obj;
		if (controlFlag == null) {
			if (other.controlFlag != null)
				return false;
		} else if (!controlFlag.equals(other.controlFlag))
			return false;
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
		if (multiMonitorControl == null) {
			if (other.multiMonitorControl != null)
				return false;
		} else if (!multiMonitorControl.equals(other.multiMonitorControl))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (phaseb != other.phaseb)
			return false;
		if (phasec != other.phasec)
			return false;
		if (usePhaseData == null) {
			if (other.usePhaseData != null)
				return false;
		} else if (!usePhaseData.equals(other.usePhaseData))
			return false;
		return true;
	}

}
