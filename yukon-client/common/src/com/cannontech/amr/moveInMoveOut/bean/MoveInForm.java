package com.cannontech.amr.moveInMoveOut.bean;

import java.util.Date;

import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.user.YukonUserContext;

public class MoveInForm{
    private String emailAddress;
    private YukonUserContext userContext;
    private String meterName;
    private String meterNumber;
    private Date moveInDate;
    private PlcMeter previousMeter;

    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    public YukonUserContext getUserContext() {
        return userContext;
    }
    public void setUserContext(YukonUserContext userContext) {
        this.userContext = userContext;
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
    
    public PlcMeter getPreviousMeter() {
        return previousMeter;
    }
    public void setPreviousMeter(PlcMeter previousMeter) {
        this.previousMeter = previousMeter;
    }
}