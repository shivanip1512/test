package com.cannontech.multispeak.data.v4;

import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.msp.beans.v4.MeterReading;
import com.cannontech.multispeak.client.MultispeakDefines;

/**
 * The base class for all BillableDevices
 */
public abstract class MeterReadBase implements ReadableDevice{

    private MeterReading meterReading;
    @Override
    public MeterReading getMeterReading(){
        if( meterReading == null) {
            meterReading = new MeterReading();
        }
        return meterReading;
    }
    
    @Override
    public void setMeterNumber(String meterNumber) {
        MeterID meterId = new MeterID();
        meterId.setMeterNo(meterNumber);
        getMeterReading().setMeterID(meterId);
        getMeterReading().setObjectID(meterNumber);
        getMeterReading().setDeviceID(meterNumber);
        getMeterReading().setUtility(MultispeakDefines.AMR_VENDOR);
    }
}
