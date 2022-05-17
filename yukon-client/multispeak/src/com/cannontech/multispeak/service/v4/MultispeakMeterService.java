package com.cannontech.multispeak.service.v4;

import java.util.List;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.MeterGroup;
import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.msp.beans.v4.MeterReading;
import com.cannontech.msp.beans.v4.MspMeter;
import com.cannontech.msp.beans.v4.RCDState;
import com.cannontech.msp.beans.v4.ServiceLocation;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;

public interface MultispeakMeterService {
    
    /**
     * This is a workaround method for SEDC.  This method is used to perform an actual meter interrogation and then return
     * the collected reading if message received within 2 minutes.
     * @throws MultispeakWebServiceException
     */
    public MeterReading getLatestReadingInterrogate(MultispeakVendor mspVendor, YukonMeter meter, String responseUrl);

    
    /**
     * Add MeterNos to SystemGroupEnum.USAGEMONITORING Device Group.
     */
    public List<ErrorObject> initiateUsageMonitoring(MultispeakVendor mspVendor, List<MeterID> meterIDs);
    
    /**
     * Remove MeterNos from SystemGroupEnum.USAGEMONITORING Device Group.
     */
    public List<ErrorObject> cancelUsageMonitoring(MultispeakVendor mspVendor, List<MeterID> meterIDs);

    /**
     * Send a ping command to pil connection for each meter in meterNumbers.
     * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
     * @throws MultispeakWebServiceException
     */
    public List<ErrorObject> odEvent(MultispeakVendor mspVendor,
            List<MeterID> meterIds,
            String transactionId, String responseUrl) throws MultispeakWebServiceException;
    
    /** Adds meters to a group. If the group doesn't exist, a new group will be created
     */
    public List<ErrorObject> addMetersToGroup(MeterGroup meterGroup, String mspMethod, MultispeakVendor mspVendor);

    /**
     * Removed meters from groupName and deletes groupName from the system.
     */
    public ErrorObject deleteGroup(String groupName, MultispeakVendor mspVendor);

    /**
     * Removes meterIDs from groupName.
     */
    public List<ErrorObject> removeMetersFromGroup(String groupName, List<MeterID> meterIds,
            MultispeakVendor mspVendor);

    /**
     * Updates the "meter" object, based on the PaoName Alias lookup value. 
     */
    public List<ErrorObject> serviceLocationChanged(MultispeakVendor vendor, List<ServiceLocation> serviceLocations);

    /**
     * Returns the CDMeterState (or connected status) for meter
     * Performs a read of DISCONNECT_STATUS attribute.
     * Waits for response, times out after mspVendor.maxRequestTimeout
     * 
     * @throws MultispeakWebServiceException
     */
    public RCDState cdMeterState(MultispeakVendor mspVendor,
            YukonMeter meter) throws MultispeakWebServiceException;

    /**
     * Add addMeters to Yukon database.
     * If the meter already exists in Yukon (looked up by MeterNumber, then Address, then DeviceName),
     * and the meter is disabled, then the meter will be updated with the new information.
     * Else, an ErrorObject will be returned.
     * If the meter does not already exist in Yukon (looked up by MeterNumber, then Address, then DeviceName),
     * then a new Meter object will be added to Yukon.
     * 
     * @throws MultispeakWebServiceException
     */
    List<ErrorObject> meterAdd(MultispeakVendor mspVendor, List<MspMeter> addMeters) throws MultispeakWebServiceException;

    /**
     * Removes the Meter from all ALternate group memberships (all children under Alternate).
     * Adds the Meter to Alternate child group based on extensionsList.
     * Requires cparm MSP_ENABLE_ALTGROUP_EXTENSION : TRUE to attempt to load alternative value.
     * Attempts to load cparm MSP_ALTGROUP_EXTENSION : XXX for extension value
     * If the alt group does not already exist, then a new Alternate sub group is created.
     * 
     * @return true if added to a new alternate cycle group.
     */
    public boolean updateAltGroup(MspMeter mspMeter, String meterNumber, YukonDevice yukonDevice,
            String mspMethod, MultispeakVendor mspVendor);

    /**
     * Removes (disables) a list of meters in Yukon.
     */
    public List<ErrorObject> meterRemove(MultispeakVendor mspVendor, List<MspMeter> removeMeters);
}
 
