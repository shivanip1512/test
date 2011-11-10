package com.cannontech.capcontrol.dao.providers.fields;

import com.cannontech.capcontrol.BankOpState;
import com.cannontech.common.pao.service.PaoTemplatePart;
import com.cannontech.common.util.CtiUtilities;

public class CapBankFields implements PaoTemplatePart {

	private BankOpState operationalState = BankOpState.SWITCHED;
    private int controlDeviceId = 0;
    private int controlPointId = 0;
    private int bankSize = 600;
    private int recloseDelay = 0;
    private int maxDailyOps = 0;
    private String typeOfSwitch = CtiUtilities.STRING_NONE;
    private String controllerType = CtiUtilities.STRING_NONE;
    private String switchManufacturer = CtiUtilities.STRING_NONE;
    private String mapLocationId = "0";
    private String maxOpDisable = "N";
    
    public BankOpState getOperationalState() {
		return operationalState;
	}
    
    public void setOperationalState(BankOpState operationalState) {
		this.operationalState = operationalState;
	}

	public String getControllerType() {
		return controllerType;
	}

	public void setControllerType(String controllerType) {
		this.controllerType = controllerType;
	}

	public int getControlDeviceId() {
		return controlDeviceId;
	}

	public void setControlDeviceId(int controlDeviceId) {
		this.controlDeviceId = controlDeviceId;
	}

	public int getControlPointId() {
		return controlPointId;
	}

	public void setControlPointId(int controlPointId) {
		this.controlPointId = controlPointId;
	}

	public int getBankSize() {
		return bankSize;
	}

	public void setBankSize(int bankSize) {
		this.bankSize = bankSize;
	}

	public String getTypeOfSwitch() {
		return typeOfSwitch;
	}

	public void setTypeOfSwitch(String typeOfSwitch) {
		this.typeOfSwitch = typeOfSwitch;
	}

	public String getSwitchManufacturer() {
		return switchManufacturer;
	}

	public void setSwitchManufacturer(String switchManufacturer) {
		this.switchManufacturer = switchManufacturer;
	}

	public String getMapLocationId() {
		return mapLocationId;
	}

	public void setMapLocationId(String mapLocationId) {
		this.mapLocationId = mapLocationId;
	}

	public int getRecloseDelay() {
		return recloseDelay;
	}

	public void setRecloseDelay(int recloseDelay) {
		this.recloseDelay = recloseDelay;
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
}
