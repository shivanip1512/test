package com.cannontech.capcontrol.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.YNBoolean;

public class Feeder {
	
	private String name;
	private String description = CtiUtilities.STRING_NONE;
	
	private PaoIdentifier paoIdentifier;
	private int currentVarLoadPointId = CtiUtilities.NONE_ZERO_ID;
    private int currentWattLoadPointId = CtiUtilities.NONE_ZERO_ID;
    private int currentVoltLoadPointId = CtiUtilities.NONE_ZERO_ID;
    private int phaseb = CtiUtilities.NONE_ZERO_ID;
    private int phasec = CtiUtilities.NONE_ZERO_ID;
    private String mapLocationId = "0";
    private YNBoolean multiMonitorControl = YNBoolean.NO;
    private YNBoolean usePhaseData = YNBoolean.NO;
    private YNBoolean controlFlag = YNBoolean.NO;
    
    public Feeder(PaoIdentifier paoIdentifier) {
    	this.paoIdentifier = paoIdentifier;
    }
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
    public int getPaoId() {
        return paoIdentifier.getPaoId();
    }
    public PaoIdentifier getPaoIdentifier() {
		return paoIdentifier;
	}
    public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
		this.paoIdentifier = paoIdentifier;
	}
    public String getMapLocationId() {
        return mapLocationId;
    }
    public void setMapLocationId(String mapLocationId) {
        this.mapLocationId = mapLocationId;
    }
    public YNBoolean getMultiMonitorControl() {
        return multiMonitorControl;
    }
    public void setMultiMonitorControl(YNBoolean multiMonitorControl) {
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
    public YNBoolean getUsePhaseData() {
        return usePhaseData;
    }
    public void setUsePhaseData(YNBoolean usePhaseData) {
        this.usePhaseData = usePhaseData;
    }
    public YNBoolean getControlFlag() {
		return controlFlag;
	}
	public void setControlFlag(YNBoolean controlFlag) {
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
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((mapLocationId == null) ? 0 : mapLocationId.hashCode());
		result = prime
				* result
				+ ((multiMonitorControl == null) ? 0 : multiMonitorControl
						.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((paoIdentifier == null) ? 0 : paoIdentifier.hashCode());
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
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
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
		if (paoIdentifier == null) {
			if (other.paoIdentifier != null)
				return false;
		} else if (!paoIdentifier.equals(other.paoIdentifier))
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
