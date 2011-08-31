package com.cannontech.capcontrol.dao.providers.fields;

import com.cannontech.common.device.DeviceScanTypesEnum;
import com.cannontech.common.pao.service.PaoTemplatePart;

public class DeviceScanRateFields implements PaoTemplatePart {
	private int intervalRate = 300;
	private int scanGroup = 0;
	private int alternateRate = 300;
	private DeviceScanTypesEnum scanType = DeviceScanTypesEnum.GENERAL;
	
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
	
	public DeviceScanTypesEnum getScanType() {
		return scanType;
	}

	public void setScanType(DeviceScanTypesEnum scanType) {
		this.scanType = scanType;
	}
}
