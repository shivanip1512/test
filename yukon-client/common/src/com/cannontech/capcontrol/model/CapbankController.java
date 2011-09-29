package com.cannontech.capcontrol.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;

public class CapbankController implements YukonPao {
	
	private PaoIdentifier paoIdentifier;
	
	private String name;
	private String description;
	private String statistics;
	private String windowType;
	private String scanType;
	
	private Integer winOpen;
	private Integer winClose;
	private Integer alternateOpen;
	private Integer alternateClose;
	private int masterAddress;
	private int slaveAddress;
	private int serialNumber;
	private int routeId;
	private int portId;
	private int intervalRate;
	private int postCommWait;	
	private int scanGroup;
	private int alternateRate;
	
	private boolean scanEnabled;
    private boolean disabled;
    private boolean alarmInhibit;
    private boolean controlInhibit;
	
	public CapbankController(PaoIdentifier paoIdentifier) {
		this.paoIdentifier = paoIdentifier;
	}

	public PaoIdentifier getPaoIdentifier() {
		return paoIdentifier;
	}
	
	public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
		this.paoIdentifier = paoIdentifier;
	}
	
	public int getId() {
		return paoIdentifier.getPaoId();
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
		return paoIdentifier.getPaoType();
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + alternateRate;
		result = prime * result + (disabled ? 1231 : 1237);
		result = prime * result + intervalRate;
		result = prime * result + masterAddress;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((paoIdentifier == null) ? 0 : paoIdentifier.hashCode());
		result = prime * result + portId;
		result = prime * result + postCommWait;
		result = prime * result + routeId;
		result = prime * result + (scanEnabled ? 1231 : 1237);
		result = prime * result + scanGroup;
		result = prime * result
				+ ((scanType == null) ? 0 : scanType.hashCode());
		result = prime * result + serialNumber;
		result = prime * result + slaveAddress;
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
		if (intervalRate != other.intervalRate)
			return false;
		if (masterAddress != other.masterAddress)
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
		return true;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatistics() {
		return statistics;
	}

	public void setStatistics(String statistics) {
		this.statistics = statistics;
	}

	public String getWindowType() {
		return windowType;
	}

	public void setWindowType(String windowType) {
		this.windowType = windowType;
	}

	public Integer getWinOpen() {
		return winOpen;
	}

	public void setWinOpen(Integer winOpen) {
		this.winOpen = winOpen;
	}

	public Integer getWinClose() {
		return winClose;
	}

	public void setWinClose(Integer winClose) {
		this.winClose = winClose;
	}

	public Integer getAlternateOpen() {
		return alternateOpen;
	}

	public void setAlternateOpen(Integer alternateOpen) {
		this.alternateOpen = alternateOpen;
	}

	public Integer getAlternateClose() {
		return alternateClose;
	}

	public void setAlternateClose(Integer alternateClose) {
		this.alternateClose = alternateClose;
	}

	public boolean isAlarmInhibit() {
		return alarmInhibit;
	}

	public void setAlarmInhibit(boolean alarmInhibit) {
		this.alarmInhibit = alarmInhibit;
	}

	public boolean isControlInhibit() {
		return controlInhibit;
	}

	public void setControlInhibit(boolean controlInhibit) {
		this.controlInhibit = controlInhibit;
	}
}
