package com.cannontech.thirdparty.service;

import com.cannontech.thirdparty.exception.GatewayCommissionException;
import com.cannontech.thirdparty.exception.ZigbeeClusterLibraryException;
import com.cannontech.thirdparty.messaging.SepControlMessage;
import com.cannontech.thirdparty.messaging.SepRestoreMessage;
import com.cannontech.thirdparty.model.ZigbeeGateway;
import com.cannontech.thirdparty.model.ZigbeeText;

public interface ZigbeeWebService {

	public String getAllDevices();
	
	public void installGateway(int gatewayId) throws GatewayCommissionException;
	
	public void removeGateway(int gatewayId);
	
	public void installStat(int gatewayId, int deviceId);
	
	public void uninstallStat(int gatewayId, int deviceId);

	/**
	 * Sends a text message to the gateway. 
     *  
	 * @param gateway
	 * @param zigbeeText
	 * @throws ZigbeeClusterLibraryException
	 */
	public void sendTextMessage(int gatewayId, ZigbeeText zigbeeText) throws ZigbeeClusterLibraryException ;
	
	/**
	 * Sends SEP Control message.
	 * 
	 * @param controlMessage
	 * @return
	 */
	public int sendSEPControlMessage(int eventId, SepControlMessage controlMessage);
	
	/**
     * Sends SEP Restore message.
     * 
     * @param restoreMessage
     * @return
     */
    public void sendSEPRestoreMessage(int eventId, SepRestoreMessage restoreMessage);
    
    /**
     * Download and process all Device Notifications on the gateway.
     * 
     * @param gateway
     */
    public void processAllDeviceNotificationsOnGateway(ZigbeeGateway gateway);
}
