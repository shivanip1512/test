package com.cannontech.stars.xml.serialize;

public class TwoWayLCRSetupInfoFactory {

	public static TwoWayLCRSetupInfo getDeviceSetupInfoForNewDevice(String nameOfNewDevice, int demandRateOfNewDevice) {
		return new TwoWayLCRSetupInfo(true, null, nameOfNewDevice, demandRateOfNewDevice);
	}
	
	public static TwoWayLCRSetupInfo getDeviceSetupInfoForExistingDevice(int deviceIdOfExistingDevice) {
		return new TwoWayLCRSetupInfo(false, deviceIdOfExistingDevice, null, null);
	}
}
