package com.cannontech.cbc.model;

import com.cannontech.common.pao.PaoType;

public class CapbankController {
	
	private int id;
	private String name;
	private int serialNumber;
	private int routeId;
	private int portId;
	private PaoType type;
	
	private int masterAddress;
	private int slaveAddress;
	private int postCommWait;
	
	private boolean scanEnabled;
	private String scanType;
	private int intervalRate;
	private int scanGroup;
	private int alternateRate;
    private boolean disabled;
	
	public CapbankController() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}

	public int getRouteId() {
		return routeId;
	}

	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}

	public int getMasterAddress() {
		return masterAddress;
	}

	public void setMasterAddress(int masterAddress) {
		this.masterAddress = masterAddress;
	}

	public int getSlaveAddress() {
		return slaveAddress;
	}

	public void setSlaveAddress(int slaveAddress) {
		this.slaveAddress = slaveAddress;
	}

	public int getPostCommWait() {
		return postCommWait;
	}

	public void setPostCommWait(int postCommWait) {
		this.postCommWait = postCommWait;
	}

	public String getScanType() {
		return scanType;
	}

	public void setScanType(String scanType) {
		this.scanType = scanType;
	}

	public int getIntervalRate() {
		return intervalRate;
	}

	public void setIntervalRate(int intervalRate) {
		this.intervalRate = intervalRate;
	}

	public int getScanGroup() {
		return scanGroup;
	}

	public void setScanGroup(int scanGroup) {
		this.scanGroup = scanGroup;
	}

	public int getAlternateRate() {
		return alternateRate;
	}

	public void setAlternateRate(int alternateRate) {
		this.alternateRate = alternateRate;
	}

	public PaoType getType() {
		return type;
	}

	public void setType(PaoType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public int getPortId() {
		return portId;
	}

	public void setPortId(int portId) {
		this.portId = portId;
	}

	public boolean isScanEnabled() {
		return scanEnabled;
	}

	public void setScanEnabled(boolean scanEnabled) {
		this.scanEnabled = scanEnabled;
	}
	
	public boolean getDisabled() {
        return disabled;
    }
    
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + alternateRate;
        result = prime * result + (disabled ? 1231 : 1237);
        result = prime * result + id;
        result = prime * result + intervalRate;
        result = prime * result + masterAddress;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + portId;
        result = prime * result + postCommWait;
        result = prime * result + routeId;
        result = prime * result + (scanEnabled ? 1231 : 1237);
        result = prime * result + scanGroup;
        result = prime * result
                 + ((scanType == null) ? 0 : scanType.hashCode());
        result = prime * result + serialNumber;
        result = prime * result + slaveAddress;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        CapbankController other = (CapbankController) obj;
        if (alternateRate != other.alternateRate)
            return false;
        if (disabled != other.disabled)
            return false;
        if (id != other.id)
            return false;
        if (intervalRate != other.intervalRate)
            return false;
        if (masterAddress != other.masterAddress)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (portId != other.portId)
            return false;
        if (postCommWait != other.postCommWait)
            return false;
        if (routeId != other.routeId)
            return false;
        if (scanEnabled != other.scanEnabled)
            return false;
        if (scanGroup != other.scanGroup)
            return false;
        if (scanType == null) {
            if (other.scanType != null)
                return false;
        } else if (!scanType.equals(other.scanType))
            return false;
        if (serialNumber != other.serialNumber)
            return false;
        if (slaveAddress != other.slaveAddress)
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    public static boolean isValidCbc(PaoType type) {
        
        switch (type) {
            case CBC_7010:
            case CBC_7011:
            case CBC_7012:
            case CBC_7020:
            case CBC_7022:
            case CBC_7023:
            case CBC_7024:
            case CBC_8020:
            case CBC_8024:
            case CBC_DNP:
            case CBC_EXPRESSCOM:
            case CBC_FP_2800:
            case CAPBANKCONTROLLER:
                return true;
            default: 
                return false;
        }
    }
    
    public static boolean isOneWayCbc(PaoType type) {
        
        switch (type) {
            case CBC_7010:
            case CBC_7011:
            case CBC_7012:
            case CBC_EXPRESSCOM:
                return true;
            default: 
                return false;
        }
    }
}
