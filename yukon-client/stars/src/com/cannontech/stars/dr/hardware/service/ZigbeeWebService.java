package com.cannontech.stars.dr.hardware.service;

public interface ZigbeeWebService {

	public String getAllDevices();
	
	public String installGateway(int deviceId);
	
	public String removeGateway(int deviceId);
	
	public String installStat(int deviceId);
	
	public String sendTextMessage(int deviceId, String message);
	
}
