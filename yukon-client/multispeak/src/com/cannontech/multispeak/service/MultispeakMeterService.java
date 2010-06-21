 /*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.service;

import java.rmi.RemoteException;

import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.multispeak.block.FormattedBlockService;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.LoadActionCode;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.deploy.service.MeterGroup;
import com.cannontech.multispeak.deploy.service.MeterRead;
import com.cannontech.multispeak.deploy.service.ServiceLocation;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface MultispeakMeterService {
	
    /** 
     * Returns the LoadActionCode (or connected status) for meter
     * @param mspVendor
     * @param meter
     * @param transactionID
     * @return
     * @throws RemoteException
     */
    public LoadActionCode CDMeterState(MultispeakVendor mspVendor, 
            com.cannontech.amr.meter.model.Meter meter,
            String transactionID) throws RemoteException;

    /**
     * This is a workaround method for SEDC.  This method is used to perform an actual meter interrogation and then return
     * the collected reading if message recieved within 2 minutes.
     * @param mspVendor
     * @param meterNumber
     * @return
     * @throws RemoteException
     */
    public MeterRead getLatestReadingInterrogate(MultispeakVendor mspVendor, 
            com.cannontech.amr.meter.model.Meter meter,
            String transactionID);

	/**
	 * Send a ping command to pil connection for each meter in meterNumbers.
	 * @param meterNumbers
	 * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
	 */
	public ErrorObject[] odEvent(MultispeakVendor vendor, 
	        String[] meterNumbers,
	        String transactionID) throws RemoteException;
	
    /**
     * Send meter read commands to pil connection for each meter in meterNumbers.
     * @param meterNumbers
     * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
     */
    public ErrorObject[] meterReadEvent(MultispeakVendor vendor, 
            String[] meterNumbers,
            String transactionID);

    /**
     * Send meter read commands to pil connection for each meter in meterNumbers.
     * @param meterNumbers
     * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
     */
    public ErrorObject[] blockMeterReadEvent(MultispeakVendor vendor, 
            String meterNumber, FormattedBlockService block, String transactionID);

    /**
     * Send a ping command to pil connection for each meter in meterNumbers.
     * @param meterNumbers
     * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
     */
    public ErrorObject[] cdEvent(MultispeakVendor vendor, 
            ConnectDisconnectEvent [] cdEvents,
            String transactionID) throws RemoteException;

    /**
     * Add MeterNos to SystemGroupEnum.DISCONNECTSTATUS Device Group. 
     * @param mspVendor
     * @param meterNos
     * @return
     */
    public ErrorObject[] initiateDisconnectedStatus(MultispeakVendor mspVendor, String[] meterNos);

    /**
     * Add MeterNos to SystemGroupEnum.USAGEMONITORING Device Group.
     * @param mspVendor
     * @param meterNos
     * @return
     */
    public ErrorObject[] initiateUsageMonitoringStatus(MultispeakVendor mspVendor, String[] meterNos);

    /**
     * Remove MeterNos from SystemGroupEnum.DISCONNECTSTATUS Device Group. 
     * @param mspVendor
     * @param meterNos
     * @return
     */
    public ErrorObject[] cancelDisconnectedStatus(MultispeakVendor mspVendor, String[] meterNos);

    /**
     * Remove MeterNos from SystemGroupEnum.USAGEMONITORING Device Group.
     * @param mspVendor
     * @param meterNos
     * @return
     */
    public ErrorObject[] cancelUsageMonitoringStatus(MultispeakVendor mspVendor, String[] meterNos);
    
    /**
     * Add addMeters to Yukon database.
     * If the meter already exists in Yukon (looked up by MeterNumber, then Address, then DeviceName), 
     *  and the meter is disabled, then the meter will be updated with the new information.
     *  Else, an ErrorObject will be returned.
     * If the meter does not already exist in Yukon (looked up by MeterNumber, then Address, then DeviceName),
     *  then a new Meter object will be added to Yukon.
     * @param mspVendor
     * @param addMeters
     * @return
     * @throws RemoteException
     */
    public ErrorObject[] addMeterObject(final MultispeakVendor mspVendor, Meter[] addMeters) throws RemoteException;

    /**
     * Removes (disables) a list of meters in Yukon.
     * @param mspVendor
     * @param removeMeters
     * @return
     */
    public ErrorObject[] removeMeterObject(MultispeakVendor mspVendor, Meter[] removeMeters);

    /**
     * Updates the "meter" object, based on the PaoName Alias lookup value. 
     * @param mspVendor
     * @param serviceLocations
     * @return
     */
    public ErrorObject[] updateServiceLocation(final MultispeakVendor mspVendor, ServiceLocation[] serviceLocations);

    /**
     * Changes the meter information.  Meter is looked up by the Physical Addres (TransponderId). 
     * @param mspVendor
     * @param changedMeters
     * @return
     * @throws RemoteException
     */
    public ErrorObject[] changeMeterObject(final MultispeakVendor mspVendor, Meter[] changedMeters) throws RemoteException;

    /**
     * Adds meters to a group.  If the group doesn't exist, a new group will be created
     * @param meterGroup
     * @return
     */
    public ErrorObject[] addMetersToGroup(MeterGroup meterGroup, MultispeakVendor mspVendor);
    
    /**
     * Removes meterNumbers from groupName.
     * @param groupName
     * @return
     */
    public ErrorObject[] removeMetersFromGroup(String groupName, String[] meterNumbers, MultispeakVendor mspVendor);
    
    /**
     * Removed meters from groupName AND deletes groupName from the system.
     * @param groupName
     * @param mspVendor
     * @return
     */
    public ErrorObject deleteGroup(String groupName, MultispeakVendor mspVendor);
    
    /**
     * Removes the Meter from all Billing group memberships (all children under Billing).
     * Adds the Meter to 'newBilling' Billing child group.  If the billing group does not already
     * exist, then a new Billing sub group is created. 
     * @return true if added to a new billing cycle group.  
     */
    public boolean updateBillingCyle(String newBilling, String meterNumber, YukonDevice yukonDevice, String logActionStr, MultispeakVendor mspVendor);
    
    /**
     * Removes the Meter from all Substation group memberships (all children under Substation).
     * Adds the Meter to 'substationName' Substation child group.  If the substation group does not already
     * exist, then a new Substation sub group is created.
     * @return true if added to a new substation group.  
     */
    public boolean updateSubstationGroup(String substationName, String meterNumber, YukonDevice yukonDevice, String logActionStr, MultispeakVendor mspVendor);
    
    /**
     * This is a shortcut method for {@link updateBillingCyle} or {@link updateSubstationGroup} when the groupParent has already been determined.
     * Removes the Meter from all 'groupParent' group memberships (all children under 'groupParent').
     * Adds the Meter to 'groupName' child group.  If the 'groupName' group does not already
     * exist, then a new 'groupName' sub group is created.
     * @return true if added to a new 'groupName' group.  
     */
    public boolean updateDeviceGroup(String groupName, StoredDeviceGroup groupParent, String meterNumber, YukonDevice yukonDevice, String logActionStr, MultispeakVendor mspVendor);
}
