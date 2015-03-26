package com.cannontech.stars.database.data.lite;

import com.cannontech.database.data.lite.LiteTypes;

public class LiteMeterHardwareBase extends LiteInventoryBase {
    
    private String meterNumber;
    private int meterTypeID;
    
    public LiteMeterHardwareBase() {
        setLiteType(LiteTypes.STARS_METERHARDWAREBASE);
    }
    
    public int getMeterTypeID() {
        return meterTypeID;
    }
    
    public void setMeterTypeID(int meterTypeID) {
        this.meterTypeID = meterTypeID;
    }
    
    public String getMeterNumber() {
        return meterNumber;
    }
    
    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }
    
}