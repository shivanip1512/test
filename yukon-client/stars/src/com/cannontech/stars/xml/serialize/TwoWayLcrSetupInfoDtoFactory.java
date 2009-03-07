package com.cannontech.stars.xml.serialize;

public class TwoWayLcrSetupInfoDtoFactory {

	public static TwoWayLcrSetupInfoDto getDeviceSetupInfoDtoForNewDevice(int yukonDeviceTypeId, String nameOfNewDevice, int demandRateOfNewDevice) {
		return new TwoWayLcrSetupInfoDto(true, yukonDeviceTypeId, null, nameOfNewDevice, demandRateOfNewDevice);
	}
	
	public static TwoWayLcrSetupInfoDto getDeviceSetupInfoDtoForExistingDevice(int yukonDeviceTypeId, int deviceIdOfExistingDevice) {
		return new TwoWayLcrSetupInfoDto(false, yukonDeviceTypeId, deviceIdOfExistingDevice, null, null);
	}
}
