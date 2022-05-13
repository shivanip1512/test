package com.cannontech.multispeak.service.v4;

import java.util.List;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.MeterGroup;
import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.msp.beans.v4.MeterReading;
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
     * Adds meters to a group. If the group doesn't exist, a new group will be created
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
}
