package com.cannontech.amr.moveInMoveOut.bean;

import java.util.Date;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.database.data.lite.LiteYukonUser;

public class MoveInForm{
    private String emailAddress;
    private LiteYukonUser liteYukonUser;
    private String meterName;
    private String meterNumber;
    private Date moveInDate;
    private Meter previousMeter;

    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    public LiteYukonUser getLiteYukonUser() {
        return liteYukonUser;
    }
    public void setLiteYukonUser(LiteYukonUser liteYukonUser) {
        this.liteYukonUser = liteYukonUser;
    }
    
    public String getMeterName() {
        return meterName;
    }
    public void setMeterName(String meterName) {
        this.meterName = meterName;
    }
    
    public String getMeterNumber() {
        return meterNumber;
    }
    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }
    
    public Date getMoveInDate() {
        return moveInDate;
    }
    public void setMoveInDate(Date moveInDate) {
        this.moveInDate = moveInDate;
    }
    
    public Meter getPreviousMeter() {
        return previousMeter;
    }
    public void setPreviousMeter(Meter previousMeter) {
        this.previousMeter = previousMeter;
    }
}