package com.cannontech.amr.moveInMoveOut.bean;

import java.util.Date;

import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.user.YukonUserContext;

public class MoveOutForm{
    private String emailAddress;
    private YukonUserContext userContext;
    private PlcMeter meter;
    private Date moveOutDate;
    
    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    public Date getMoveOutDate() {
        return moveOutDate;
    }
    public void setMoveOutDate(Date moveOutDate) {
        this.moveOutDate = moveOutDate;
    }
    public YukonUserContext getUserContext() {
        return userContext;
    }
    public void setUserContext(YukonUserContext userContext) {
        this.userContext = userContext;
    }
    public PlcMeter getMeter() {
        return meter;
    }
    public void setMeter(PlcMeter meter) {
        this.meter = meter;
    }
}
