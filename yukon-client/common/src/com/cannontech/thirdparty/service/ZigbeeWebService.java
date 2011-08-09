package com.cannontech.thirdparty.service;

import java.util.Map;

import com.cannontech.common.model.ZigbeeTextMessage;
import com.cannontech.thirdparty.digi.exception.DigiWebServiceException;
import com.cannontech.thirdparty.exception.ZigbeeClusterLibraryException;
import com.cannontech.thirdparty.exception.ZigbeeCommissionException;
import com.cannontech.thirdparty.messaging.SepControlMessage;
import com.cannontech.thirdparty.messaging.SepRestoreMessage;
import com.cannontech.thirdparty.model.DRLCClusterAttribute;

public interface ZigbeeWebService extends ZigbeeStateUpdaterService {

	/**
	 * Sends the install commands for the gateway to the third party.
	 * 
	 * @param gatewayId
	 * @throws ZigbeeCommissionException
	 */
	public void installGateway(int gatewayId) throws ZigbeeCommissionException;
	
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
	public void installEndPoint(int gatewayId, int deviceId);
	
	/**
	 * Sends the uninstall device commands to the third party.
	 * 
	 * @param gatewayId
	 * @param deviceId
	 */
	public void uninstallEndPoint(int gatewayId, int deviceId);

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
    
}
