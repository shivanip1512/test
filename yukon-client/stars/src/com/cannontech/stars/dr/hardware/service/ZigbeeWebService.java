package com.cannontech.stars.dr.hardware.service;

public interface ZigbeeWebService {

	public String getAllDevices();
	
	public String installGateway(int deviceId);
	
	public String removeGateway(int deviceId);
	
	public String installStat(int deviceId, int gatewayId);
	
	public String uninstallStat(int deviceId, int gatewayId);
	
	public String sendTextMessage(int deviceId, int gatewayId, String message);
	
}
