package com.cannontech.stars.dr.thirdparty.digi.service;

public interface ZigbeeWebService {

	public String getAllDevices();
	
	public void installGateway(int deviceId);
	
	public void removeGateway(int deviceId);
	
	public void installStat(int deviceId, int gatewayId);
	
	public void uninstallStat(int deviceId, int gatewayId);
	
	public void sendTextMessage(int deviceId, int gatewayId, String message);
	
}
