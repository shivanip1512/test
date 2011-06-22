package com.cannontech.thirdparty.service;

import java.net.SocketTimeoutException;
import java.util.Map;

import com.cannontech.common.model.ZigbeeTextMessage;
import com.cannontech.thirdparty.exception.DigiWebServiceException;
import com.cannontech.thirdparty.exception.GatewayCommissionException;
import com.cannontech.thirdparty.exception.ZigbeeClusterLibraryException;
import com.cannontech.thirdparty.messaging.SepControlMessage;
import com.cannontech.thirdparty.messaging.SepRestoreMessage;
import com.cannontech.thirdparty.model.DRLCClusterAttribute;
import com.cannontech.thirdparty.model.ZigbeeDevice;

public interface ZigbeeWebService {

    /**
     * Updates the connection status point on every gateway in the system.
     * 
     */
    public void refreshAllGateways();
    
    /**
     * Updates the connection and commission status point for the passed in gateway.
     * 
     * @param gateway
     */
	public void refreshGateway(ZigbeeDevice gateway);
	
	/**
	 * Pings the device specified and updates the connection status point based on the results.
	 * If just the device does not respond, only it will be marked disconnected. If the gateway is not 
	 * responding BOTH the device and gateway will be marked disconnected.
	 * 
	 * Does not affect the commission
	 *  
	 * @param device
	 */
	public void refreshDeviceStatuses(ZigbeeDevice device);

	/**
	 * Sends the install commands for the gateway to the third party.
	 * 
	 * @param gatewayId
	 * @throws GatewayCommissionException
	 */
	public void installGateway(int gatewayId) throws GatewayCommissionException;
	
	/**
	 * Sends the uninstall commands for the gateway to the third party.
	 * 
	 * @param gatewayId
	 */
	public void removeGateway(int gatewayId);
	
	/**
	 * Sends the install device commands to the third party.
	 * 
	 * @param gatewayId
	 * @param deviceId
	 */
	public void installStat(int gatewayId, int deviceId);
	
	/**
	 * Sends the uninstall device commands to the third party.
	 * 
	 * @param gatewayId
	 * @param deviceId
	 */
	public void uninstallStat(int gatewayId, int deviceId);

	/**
	 * Sends the device level addressing configuration attributes to the device specified.
	 * @param deviceId
	 * @param attributes
	 */
	public void sendLoadGroupAddressing(int deviceId, Map<DRLCClusterAttribute,Integer> attributes);
	
	/**
	 * Sends a text message to the gateway. 
     *  
	 * @param gateway
	 * @param zigbeeText
	 * @throws ZigbeeClusterLibraryException
	 */
	public void sendTextMessage(ZigbeeTextMessage message) throws ZigbeeClusterLibraryException, DigiWebServiceException ;
	
	/**
	 * Sends a manual adjustment to the gateway.
	 * 
	 * @param message
	 * @throws ZigbeeClusterLibraryException
	 * @throws DigiWebServiceException
	 */
	public void sendManualAdjustment(ZigbeeTextMessage message) throws ZigbeeClusterLibraryException, DigiWebServiceException ;
	
	/**
	 * Sends Control Message.
	 * 
	 * @param eventId
	 * @param controlMessage
	 * @return
	 */
	public void sendSEPControlMessage(int eventId, SepControlMessage controlMessage);
	
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
     * @throws SocketTimeoutException 
     */
    public void processAllDeviceNotificationsOnGateway(ZigbeeDevice gateway) throws SocketTimeoutException;
    
}
