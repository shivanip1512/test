package com.cannontech.multispeak.service.v4;

import java.util.List;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.msp.beans.v4.MeterReading;
import com.cannontech.msp.beans.v4.MspMeter;
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
     * Updates the "meter" object, based on the PaoName Alias lookup value. 
     */
    public List<ErrorObject> serviceLocationChanged(MultispeakVendor vendor, List<ServiceLocation> serviceLocations);
    
    /**
     * Add addMeters to Yukon database.
     * If the meter already exists in Yukon (looked up by MeterNumber, then Address, then DeviceName), 
     *  and the meter is disabled, then the meter will be updated with the new information.
     *  Else, an ErrorObject will be returned.
     * If the meter does not already exist in Yukon (looked up by MeterNumber, then Address, then DeviceName),
     *  then a new Meter object will be added to Yukon.
     * @throws MultispeakWebServiceException
     */
    List<ErrorObject> meterAdd(MultispeakVendor mspVendor, List<MspMeter> addMeters) throws MultispeakWebServiceException;

    /**
     * Removes the Meter from all ALternate group memberships (all children under Alternate).
     * Adds the Meter to Alternate child group based on extensionsList. 
     * Requires cparm MSP_ENABLE_ALTGROUP_EXTENSION : TRUE to attempt to load alternative value.
     * Attempts to load cparm MSP_ALTGROUP_EXTENSION : XXX for extension value
     * If the alt group does not already exist, then a new Alternate sub group is created. 
     * @return true if added to a new alternate cycle group.  
     */
    public boolean updateAltGroup(MspMeter mspMeter, String meterNumber, YukonDevice yukonDevice,
            String mspMethod, MultispeakVendor mspVendor);

}
 
