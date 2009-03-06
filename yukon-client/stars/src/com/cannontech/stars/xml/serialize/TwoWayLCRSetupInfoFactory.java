package com.cannontech.stars.xml.serialize;

public class TwoWayLcrSetupInfoFactory {

	public static TwoWayLcrSetupInfo getDeviceSetupInfoForNewDevice(int yukonDeviceTypeId, String nameOfNewDevice, int demandRateOfNewDevice) {
		return new TwoWayLcrSetupInfo(true, yukonDeviceTypeId, null, nameOfNewDevice, demandRateOfNewDevice);
	}
	
	public static TwoWayLcrSetupInfo getDeviceSetupInfoForExistingDevice(int yukonDeviceTypeId, int deviceIdOfExistingDevice) {
		return new TwoWayLcrSetupInfo(false, yukonDeviceTypeId, deviceIdOfExistingDevice, null, null);
	}
}
