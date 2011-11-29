package com.cannontech.common.pao.service.providers.fields;

import com.cannontech.common.device.DeviceScanType;
import com.cannontech.common.pao.service.PaoTemplatePart;

public class DeviceScanRateFields implements PaoTemplatePart {
	private int intervalRate = 300;
	private int scanGroup = 0;
	private int alternateRate = 300;
	private DeviceScanType scanType = DeviceScanType.GENERAL;
	
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
	
	public DeviceScanType getScanType() {
		return scanType;
	}

	public void setScanType(DeviceScanType scanType) {
		this.scanType = scanType;
	}
}
