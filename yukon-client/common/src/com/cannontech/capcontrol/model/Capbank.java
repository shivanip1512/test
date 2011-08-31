package com.cannontech.capcontrol.model;

import com.cannontech.capcontrol.CapBankOperationalState;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;

public class Capbank implements YukonPao {
	
	private String name;
	private String description;
	
    private PaoIdentifier paoIdentifier;
    private CapBankOperationalState operationalState;
    private int controlDeviceId;
    private int controlPointId;
    private int bankSize;
    private int recloseDelay;
    private int maxDailyOps;
    private String controllerType;
    private String typeOfSwitch;
    private String switchManufacturer;
    private String mapLocationId;
    private String maxOpDisable;
    
    private boolean disabled = false;
    
    public Capbank(PaoIdentifier paoIdentifier) {
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
    
    public int getPaoId() {
        return paoIdentifier.getPaoId();
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
    
    public String getMaxOpDisable() {
        return maxOpDisable;
    }
    
    public void setMaxOpDisable(String maxOpDisable) {
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
	
	public PaoIdentifier getPaoIdentifier() {
		return paoIdentifier;
	}
	
	public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
		this.paoIdentifier = paoIdentifier;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bankSize;
		result = prime * result + controlDeviceId;
		result = prime * result + controlPointId;
		result = prime * result
				+ ((controllerType == null) ? 0 : controllerType.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + (disabled ? 1231 : 1237);
		result = prime * result
				+ ((mapLocationId == null) ? 0 : mapLocationId.hashCode());
		result = prime * result + maxDailyOps;
		result = prime * result
				+ ((maxOpDisable == null) ? 0 : maxOpDisable.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime
				* result
				+ ((operationalState == null) ? 0 : operationalState.hashCode());
		result = prime * result
				+ ((paoIdentifier == null) ? 0 : paoIdentifier.hashCode());
		result = prime * result + recloseDelay;
		result = prime
				* result
				+ ((switchManufacturer == null) ? 0 : switchManufacturer
						.hashCode());
		result = prime * result
				+ ((typeOfSwitch == null) ? 0 : typeOfSwitch.hashCode());
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
		Capbank other = (Capbank) obj;
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
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (disabled != other.disabled)
			return false;
		if (mapLocationId == null) {
			if (other.mapLocationId != null)
				return false;
		} else if (!mapLocationId.equals(other.mapLocationId))
			return false;
		if (maxDailyOps != other.maxDailyOps)
			return false;
		if (maxOpDisable == null) {
			if (other.maxOpDisable != null)
				return false;
		} else if (!maxOpDisable.equals(other.maxOpDisable))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (operationalState != other.operationalState)
			return false;
		if (paoIdentifier == null) {
			if (other.paoIdentifier != null)
				return false;
		} else if (!paoIdentifier.equals(other.paoIdentifier))
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
