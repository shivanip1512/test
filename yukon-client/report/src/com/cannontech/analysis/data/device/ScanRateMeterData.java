/*
 * Created on May 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.cannontech.analysis.data.device;

import com.cannontech.database.db.device.DeviceScanRate;

public class ScanRateMeterData
{
    private MeterAndPointData meterAndPointData = null;
    private DeviceScanRate deviceScanRate = null;
	
    public ScanRateMeterData(MeterAndPointData meterAndPointData, DeviceScanRate deviceScanRate) {
		super();
		this.meterAndPointData = meterAndPointData;
		this.deviceScanRate = deviceScanRate;
	}

	public DeviceScanRate getDeviceScanRate() {
		return deviceScanRate;
	}

	public void setDeviceScanRate(DeviceScanRate deviceScanRate) {
		this.deviceScanRate = deviceScanRate;
	}

	public MeterAndPointData getMeterAndPointData() {
		return meterAndPointData;
	}

	public void setMeterAndPointData(MeterAndPointData meterAndPointData) {
		this.meterAndPointData = meterAndPointData;
	}
}
