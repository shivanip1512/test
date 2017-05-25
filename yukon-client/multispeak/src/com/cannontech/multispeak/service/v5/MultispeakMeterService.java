package com.cannontech.multispeak.service.v5;

import java.util.List;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.msp.beans.v5.commontypes.ErrorObject;
import com.cannontech.msp.beans.v5.commontypes.MeterID;
import com.cannontech.msp.beans.v5.enumerations.EndDeviceStateKind;
import com.cannontech.msp.beans.v5.enumerations.RCDStateKind;
import com.cannontech.msp.beans.v5.multispeak.ConnectDisconnectEvent;
import com.cannontech.msp.beans.v5.multispeak.ElectricMeter;
import com.cannontech.msp.beans.v5.multispeak.ElectricMeterExchange;
import com.cannontech.msp.beans.v5.multispeak.MeterGroup;
import com.cannontech.msp.beans.v5.multispeak.ObjectDeletion;
import com.cannontech.msp.beans.v5.multispeak.ServiceLocation;
import com.cannontech.multispeak.block.v5.Block;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.v5.FormattedBlockProcessingService;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

public interface MultispeakMeterService {
    /**
     * Removes the Meter from all Billing group memberships (all children under Billing).
     * Adds the Meter to 'newBilling' Billing child group. If the billing group does not already
     * exist, then a new Billing sub group is created.
     * 
     * @return true if added to a new billing cycle group.
     */
    public boolean updateBillingCyle(String newBilling, String meterNumber, YukonDevice yukonDevice, String mspMethod,
            MultispeakVendor mspVendor);

    /**
     * Removes the Meter from all Substation group memberships (all children under Substation).
     * Adds the Meter to 'substationName' Substation child group. If the substation group does not already
     * exist, then a new Substation sub group is created.
     * 
     * @return true if added to a new substation group.
     */
    public boolean updateSubstationGroup(String substationName, String meterNumber, YukonDevice yukonDevice,
            String mspMethod, MultispeakVendor mspVendor);
    
    /**
     * Updates the "meter" object, based on the PaoName Alias lookup value. 
     */
    public List<ErrorObject> serviceLocationsChanged(final MultispeakVendor mspVendor, List<ServiceLocation> changedServiceLocations) throws MultispeakWebServiceException;
    
    /**
     * Delete a list of meters in Yukon.
     */
    public List<ErrorObject> metersDeleted(MultispeakVendor vendor, List<ObjectDeletion> electricMeters) throws MultispeakWebServiceException;
    
    /**
     * Changes the meter information.  Meter is looked up by the Physical Address (TransponderId). 
     * @throws MultispeakWebServiceException
     */
    public List<ErrorObject> metersChanged(MultispeakVendor vendor, List<ElectricMeter> electricChangedMeters) throws MultispeakWebServiceException;
    
    /**
     * Disable a list of meters in Yukon.
     */
    public List<ErrorObject> metersUninstalled(MultispeakVendor vendor, List<ElectricMeter> electricUninstalledMeters) throws MultispeakWebServiceException;

    /**
     * Create new meter if not present in database but
     * If the meter already exists in Yukon (looked up by MeterNumber, then Address, then DeviceName), 
     *  and the meter is disabled, then the meter will be updated with the new information.
     *  Else, an ErrorObject will be returned.
     * If the meter does not already exist in Yukon (looked up by MeterNumber, then Address, then DeviceName),
     *  then a new Meter object will be added to Yukon.
     * @throws MultispeakWebServiceException
     */
    public List<ErrorObject> metersInstalled(MultispeakVendor vendor, List<ElectricMeter> electricInstalledMeters) throws MultispeakWebServiceException;
    
    /**
     * Create new meters in Yukon database.
     * If the meter already exists in Yukon (looked up by MeterNumber, then Address, then DeviceName), 
     * If the meter does not already exist in Yukon (looked up by MeterNumber, then Address, then DeviceName),
     *  then a new Meter object will be added to Yukon.
     * @throws MultispeakWebServiceException
     */
    public List<ErrorObject> metersCreated(MultispeakVendor vendor, List<ElectricMeter> electricCreatedMeters) throws MultispeakWebServiceException;

    /**
     * Disable a existing meter that exists in yukon and create a new meter (replacement of existing meter)
     */
    public List<ErrorObject> metersExchanged(MultispeakVendor vendor, List<ElectricMeterExchange> exchangeMeters) throws MultispeakWebServiceException;
    
    /**
	 * Send a ping command to pil connection for each meter in meterNumbers.
	 * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
	 * @throws MultispeakWebServiceException 
	 */
	public List<ErrorObject> odEvent(MultispeakVendor mspVendor, 
			List<String> meterNumbers,
	        String transactionID, String responseUrl) throws MultispeakWebServiceException;
	
	/**
     * Returns an ImmutableSet of supported EndDeviceStateTypes. Currently these are being initialized
     * to the following:
     * <ul>
     * <li>EndDeviceStateKind.Outage
     * <li>EndDeviceStateKind.NO_RESPONSE
     * <li>EndDeviceStateKind.STARTING_UP
     * <li>EndDeviceStateKind.IN_SERVICE,
     * <li>EndDeviceStateKind.DEFECTIVE
     * <li>EndDeviceStateKind.OUTOF_SERVICE
     * <li>EndDeviceStateKind.SHUTTING_DOWN
     * </ul>
     */
    public ImmutableSet<EndDeviceStateKind> getsupportedEndDeviceStateTypes();

    /**
     * Returns an ImmutableSetMultimap of the EndDeviceStateKind and which error codes are associated
     * with them.
     * <p>
     * By default we assign error codes to the Outage and Restoration OutageEventTypes:
     * <ul>
     * <li>EndDeviceStateKind.Outage: 20, 57, 72
     * <li>EndDeviceStateKind.STARTING_UP: 1, 17, 74, 0
     * </ul>
     * This is done in the initialize() method. The user can override these defaults using cparms.
     * The syntax for this is "MSP_OUTAGE_EVENT_TYPE_CONFIG_xxx : errCode1, errorCode2, etc" where
     * "xxx" is the name of the EndDeviceStateKind that is being overridden.
     * <p>
     * Example: "MSP_OUTAGE_EVENT_TYPE_CONFIG_OUTAGE : 17". This will remove the error code 17 from
     * any other EndDeviceStateKind it is associated with and assign 17 to the EndDeviceStateKind.Outage
     * map entry. So, since 17 is by default an error code associated with
     * EndDeviceStateKind.STARTING_UP... including this cparm would remove 17 from the STARTING_UP map
     * entry and re-assign it to EndDeviceStateKind.Outage entry.
     * <p>
     * If an incoming error code is not found in the Outage or STARTING_UP default codes above, then an
     * EndDeviceStateKind of NoResponse is assigned.
     * 
     * @return ImmutableSetMultimap<OutageEventType, Integer>
     * @see getsupportedEndDeviceStateTypes() method for a list of the possible EndDeviceStateKind that can be
     *      contained in this map.
     */
    public ImmutableSetMultimap<EndDeviceStateKind, Integer> getOutageConfig();
    
    /** 
     * Returns the RCDStateKind (or connected status) for meter
     * Performs a read of DISCONNECT_STATUS attribute. 
     * Waits for response, times out after mspVendor.maxRequestTimeout 
     * @throws MultispeakWebServiceException
     */
    public RCDStateKind CDMeterState(MultispeakVendor mspVendor, 
            YukonMeter meter) throws MultispeakWebServiceException;
    
    /**
     * Send a disconnect/connect request to Porter (PLC) or submit to queue (RFN) for each meter in meterNumbers.
     * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
     */
    public List<ErrorObject> cdEvent(MultispeakVendor mspVendor, 
            List<ConnectDisconnectEvent> cdEvents,
            String transactionID, String responseURL) throws MultispeakWebServiceException;
    
    /**
     * Initiate reads for all meterIds and fire ReadingChangedNotification on
     * callback. Callback fires for each completed read, may have multiple per
     * meterId.
     * @return ErrorObject [] Array of errorObjects for meters that cannot be
     *         found, etc.
     */
    public List<ErrorObject> meterReadEvent(MultispeakVendor mspVendor, List<MeterID> meterIds,
            String transactionID, String responseUrl);

    /**
     * Initiate reads for meterNumber and fire FormattedBlockChangeNotification
     * on callback. Callback fires for all completed reads, will have only one
     * for meterNumber.
     * @return ErrorObject [] Array of errorObjects for meters that cannot be
     *         found, etc.
     */
    public List<ErrorObject> blockMeterReadEvent(MultispeakVendor mspVendor,
            List<MeterID> meterNumber, FormattedBlockProcessingService<Block> blockProcessingService,
            String transactionId, String responseUrl);

    /**
     * Add MeterNos to SystemGroupEnum.USAGEMONITORING Device Group.
     */
    public List<ErrorObject> initiateEndDeviceEventMonitoring(MultispeakVendor mspVendor,
            List<MeterID> meterIDs);

    /**
     * Remove MeterID from SystemGroupEnum.USAGEMONITORING Device Group.
     */
    public List<ErrorObject> cancelEndDeviceEventMonitoring(MultispeakVendor mspVendor,
            String meterID);

    /**
     * Add MeterIDs to SystemGroupEnum.DISCONNECTSTATUS Device Group.
     */
    public List<ErrorObject> setDisconnectedStatus(MultispeakVendor mspVendor,
            List<MeterID> meterIDs);

    /**
     * Remove MeterIDs from SystemGroupEnum.DISCONNECTSTATUS Device Group.
     */
    public List<ErrorObject> clearDisconnectedStatus(MultispeakVendor mspVendor,
            List<MeterID> meterIDs);

    /**
     * Removed meters from groupName AND deletes groupName from the system.
     */
    public ErrorObject deleteMeterGroups(String groupName, MultispeakVendor mspVendor);

    /**
     * Removes meterIDs from groupName.
     */
    public List<ErrorObject> removeMetersFromGroup(String groupName, List<MeterID> meterIDs,
            MultispeakVendor mspVendor);

    /**
     * Adds meters to a group. If the group doesn't exist, a new group will be
     * created
     */
    public List<ErrorObject> addMetersToGroup(List<MeterGroup> meterGroup, String mspMethod,
            MultispeakVendor mspVendor);
}
