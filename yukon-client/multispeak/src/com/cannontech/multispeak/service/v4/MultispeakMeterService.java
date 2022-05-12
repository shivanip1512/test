package com.cannontech.multispeak.service.v4;

import java.util.List;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.msp.beans.v4.MeterReading;
import com.cannontech.msp.beans.v4.RCDState;
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
     * Returns the CDMeterState (or connected status) for meter
     * Performs a read of DISCONNECT_STATUS attribute.
     * Waits for response, times out after mspVendor.maxRequestTimeout
     * 
     * @throws MultispeakWebServiceException
     */
    public RCDState cdMeterState(MultispeakVendor mspVendor,
            YukonMeter meter) throws MultispeakWebServiceException;
}
