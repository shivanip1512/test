package com.cannontech.capcontrol.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;

public class SubstationBus implements YukonPao {
	
    private PaoIdentifier paoIdentifier;
    private int currentVarLoadPointId;
    private int currentWattLoadPointId;
    private int currentVoltLoadPointId;
    private int altSubId;
    private int switchPointId;
    private int voltReductionPointId;
    private int disabledPointId;
    private int phaseb;
    private int phasec;
    private String name;
    private String description;
    private String mapLocationId;
    private String controlFlag;
    private String dualBusEnabled;
    private String multiMonitorControl;
    private String usephasedata;
    private boolean disabled;
    
    public SubstationBus(PaoIdentifier paoIdentifier) {
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
	
	public boolean getDisabled() {
        return disabled ;
    }
    
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
    
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
    
	public String getDualBusEnabled() {
        return dualBusEnabled;
    }
    
	public void setDualBusEnabled(String dualBusEnabled) {
        this.dualBusEnabled = dualBusEnabled;
    }
    
	public int getId() {
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
    
	public int getSwitchPointId() {
        return switchPointId;
    }
    
	public void setSwitchPointId(int switchPointId) {
        this.switchPointId = switchPointId;
    }
    
	public String getUsephasedata() {
        return usephasedata;
    }
    
	public void setUsephasedata(String usephasedata) {
        this.usephasedata = usephasedata;
    }

    public String getControlFlag() {
		return controlFlag;
	}
	
    public void setControlFlag(String controlFlag) {
		this.controlFlag = controlFlag;
	}
	
	public int getVoltReductionPointId() {
		return voltReductionPointId;
	}
	
	public void setVoltReductionPointId(int voltReductionPointId) {
		this.voltReductionPointId = voltReductionPointId;
	}
	
	public int getDisabledPointId() {
        return disabledPointId;
    }
	
    public void setDisabledPointId(int disabledPointId) {
        this.disabledPointId = disabledPointId;
    }
	
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + altSubId;
		result = prime * result
				+ ((controlFlag == null) ? 0 : controlFlag.hashCode());
		result = prime * result + currentVarLoadPointId;
		result = prime * result + currentVoltLoadPointId;
		result = prime * result + currentWattLoadPointId;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + (disabled ? 1231 : 1237);
		result = prime * result + disabledPointId;
		result = prime * result
				+ ((dualBusEnabled == null) ? 0 : dualBusEnabled.hashCode());
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
		result = prime * result + switchPointId;
		result = prime * result
				+ ((usephasedata == null) ? 0 : usephasedata.hashCode());
		result = prime * result + voltReductionPointId;
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
		SubstationBus other = (SubstationBus) obj;
		if (altSubId != other.altSubId)
			return false;
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
		if (disabled != other.disabled)
			return false;
		if (disabledPointId != other.disabledPointId)
			return false;
		if (dualBusEnabled == null) {
			if (other.dualBusEnabled != null)
				return false;
		} else if (!dualBusEnabled.equals(other.dualBusEnabled))
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
		if (switchPointId != other.switchPointId)
			return false;
		if (usephasedata == null) {
			if (other.usephasedata != null)
				return false;
		} else if (!usephasedata.equals(other.usephasedata))
			return false;
		if (voltReductionPointId != other.voltReductionPointId)
			return false;
		return true;
	}
}