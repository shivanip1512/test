package com.cannontech.multispeak.service;

import java.rmi.RemoteException;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.FormattedBlockProcessingService;
import com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.LoadActionCode;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.deploy.service.MeterGroup;
import com.cannontech.multispeak.deploy.service.MeterRead;
import com.cannontech.multispeak.deploy.service.OutageEventType;
import com.cannontech.multispeak.deploy.service.ServiceLocation;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

public interface MultispeakMeterService {
	
    /** 
     * Returns the LoadActionCode (or connected status) for meter
     * Performs a read of DISCONNECT_STATUS attribute. 
     * Waits for response, times out after mspVendor.maxRequestTimeout 
     * @throws RemoteException
     */
    public LoadActionCode CDMeterState(MultispeakVendor mspVendor, 
            YukonMeter meter) throws RemoteException;

    /**
     * This is a workaround method for SEDC.  This method is used to perform an actual meter interrogation and then return
     * the collected reading if message received within 2 minutes.
     * @throws RemoteException
     */
    public MeterRead getLatestReadingInterrogate(MultispeakVendor mspVendor, YukonMeter meter, String responseUrl);

	/**
	 * Send a ping command to pil connection for each meter in meterNumbers.
	 * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
	 */
	public ErrorObject[] odEvent(MultispeakVendor mspVendor, 
	        String[] meterNumbers,
	        String transactionID, String responseUrl) throws RemoteException;
	
    /**
     * Initiate reads for all meterNumber and fire ReadingChangedNotification on callback.
     * Callback fires for each completed read, may have multiple per meterNumber.
     * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
     */
    public ErrorObject[] meterReadEvent(MultispeakVendor mspVendor, 
            String[] meterNumbers,
            String transactionID, String responseUrl);

    /**
     * Initiate reads for meterNumber and fire FormattedBlockChangeNotification on callback.
     * Callback fires for all completed reads, will have only one for meterNumber. 
     * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
     */
    public ErrorObject[] blockMeterReadEvent(MultispeakVendor mspVendor,
                                             String meterNumber,
                                             FormattedBlockProcessingService<Block> blockProcessingService,
                                             String transactionId, String responseUrl);
    
    /**
     * Send a disconnect/connect request to Porter (PLC) or submit to queue (RFN) for each meter in meterNumbers.
     * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
     */
    public ErrorObject[] cdEvent(MultispeakVendor mspVendor, 
            ConnectDisconnectEvent [] cdEvents,
            String transactionID, String responseURL) throws RemoteException;

    /**
     * Add MeterNos to SystemGroupEnum.DISCONNECTSTATUS Device Group. 
     */
    public ErrorObject[] initiateDisconnectedStatus(MultispeakVendor mspVendor, String[] meterNos);

    /**
     * Add MeterNos to SystemGroupEnum.USAGEMONITORING Device Group.
     */
    public ErrorObject[] initiateUsageMonitoring(MultispeakVendor mspVendor, String[] meterNos);

    /**
     * Remove MeterNos from SystemGroupEnum.DISCONNECTSTATUS Device Group. 
     */
    public ErrorObject[] cancelDisconnectedStatus(MultispeakVendor mspVendor, String[] meterNos);

    /**
     * Remove MeterNos from SystemGroupEnum.USAGEMONITORING Device Group.
     */
    public ErrorObject[] cancelUsageMonitoring(MultispeakVendor mspVendor, String[] meterNos);
    
    /**
     * Add addMeters to Yukon database.
     * If the meter already exists in Yukon (looked up by MeterNumber, then Address, then DeviceName), 
     *  and the meter is disabled, then the meter will be updated with the new information.
     *  Else, an ErrorObject will be returned.
     * If the meter does not already exist in Yukon (looked up by MeterNumber, then Address, then DeviceName),
     *  then a new Meter object will be added to Yukon.
     * @throws RemoteException
     */
    public ErrorObject[] meterAdd(final MultispeakVendor mspVendor, Meter[] addMeters) throws RemoteException;

    /**
     * Removes (disables) a list of meters in Yukon.
     */
    public ErrorObject[] meterRemove(MultispeakVendor mspVendor, Meter[] removeMeters);

    /**
     * Updates the "meter" object, based on the PaoName Alias lookup value. 
     */
    public ErrorObject[] serviceLocationChanged(final MultispeakVendor mspVendor, ServiceLocation[] serviceLocations);

    /**
     * Changes the meter information.  Meter is looked up by the Physical Address (TransponderId). 
     * @throws RemoteException
     */
    public ErrorObject[] meterChanged(final MultispeakVendor mspVendor, Meter[] changedMeters) throws RemoteException;

    /**
     * Adds meters to a group.  If the group doesn't exist, a new group will be created
     */
    public ErrorObject[] addMetersToGroup(MeterGroup meterGroup, MultispeakVendor mspVendor);
    
    /**
     * Removes meterNumbers from groupName.
     */
    public ErrorObject[] removeMetersFromGroup(String groupName, String[] meterNumbers, MultispeakVendor mspVendor);
    
    /**
     * Removed meters from groupName AND deletes groupName from the system.
     */
    public ErrorObject deleteGroup(String groupName, MultispeakVendor mspVendor);
    
    /**
     * Removes the Meter from all Billing group memberships (all children under Billing).
     * Adds the Meter to 'newBilling' Billing child group.  If the billing group does not already
     * exist, then a new Billing sub group is created. 
     * @return true if added to a new billing cycle group.  
     */
    public boolean updateBillingCyle(String newBilling, String meterNumber, YukonDevice yukonDevice, String mspMethod, MultispeakVendor mspVendor);
    
    /**
     * Removes the Meter from all ALternate group memberships (all children under Alternate).
     * Adds the Meter to Alternate child group based on extensionsList. 
     * Requires cparm MSP_ENABLE_ALTGROUP_EXTENSION : TRUE to attempt to load alternative value.
     * Attempts to load cparm MSP_ALTGROUP_EXTENSION : XXX for extension value
     * If the alt group does not already exist, then a new Alternate sub group is created. 
     * @return true if added to a new alternate cycle group.  
     */
    public boolean updateAltGroup(ServiceLocation mspServiceLocation, String meterNumber, YukonDevice yukonDevice, String mspMethod, MultispeakVendor mspVendor);
        
    /**
     * Removes the Meter from all Substation group memberships (all children under Substation).
     * Adds the Meter to 'substationName' Substation child group.  If the substation group does not already
     * exist, then a new Substation sub group is created.
     * @return true if added to a new substation group.  
     */
    public boolean updateSubstationGroup(String substationName, String meterNumber, YukonDevice yukonDevice, String mspMethod, MultispeakVendor mspVendor);

    /**
     * Returns an ImmutableSet of supported OutageEventTypes. Currently these are being initialized
     * to the following:
     * <ul>
     * <li>OutageEventType.Outage
     * <li>OutageEventType.NoResponse
     * <li>OutageEventType.Restoration
     * <li>OutageEventType.PowerOff
     * <li>OutageEventType.PowerOn
     * <li>OutageEventType.Instantaneous
     * <li>OutageEventType.Inferred
     * </ul>
     */
    public ImmutableSet<OutageEventType> getSupportedEventTypes();

    /**
     * Returns an ImmutableSetMultimap of the OutageEventTypes and which error codes are associated
     * with them.
     * <p>
     * By default we assign error codes to the Outage and Restoration OutageEventTypes:
     * <ul>
     * <li>OutageEventType.Outage: 20, 57, 72
     * <li>OutageEventType.Restoration: 1, 17, 74, 0
     * </ul>
     * This is done in the initialize() method. The user can override these defaults using cparms.
     * The syntax for this is "MSP_OUTAGE_EVENT_TYPE_CONFIG_xxx : errCode1, errorCode2, etc" where
     * "xxx" is the name of the OutageEventType that is being overridden.
     * <p>
     * Example: "MSP_OUTAGE_EVENT_TYPE_CONFIG_OUTAGE : 17". This will remove the error code 17 from
     * any other OutageEventType it is associated with and assign 17 to the OutageEventType.Outage
     * map entry. So, since 17 is by default an error code associated with
     * OutageEventType.Restoration... including this cparm would remove 17 from the Restoration map
     * entry and re-assign it to OutageEventType.Outage entry.
     * <p>
     * If an incoming error code is not found in the Outage or Restoration default codes above, then an
     * OutageEventType of NoResponse is assigned.
     * 
     * @return ImmutableSetMultimap<OutageEventType, Integer>
     * @see getSupportedEventTypes() method for a list of the possible OutageEventTypes that can be
     *      contained in this map.
     */
    public ImmutableSetMultimap<OutageEventType, Integer> getOutageConfig();
}
