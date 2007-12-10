package com.cannontech.amr.moveInMoveOut.bean;

import java.util.Date;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.database.data.lite.LiteYukonUser;

public class MoveOutForm{
    private String emailAddress;
    private LiteYukonUser liteYukonUser;
    private Meter meter;
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
    public LiteYukonUser getLiteYukonUser() {
        return liteYukonUser;
    }
    public void setLiteYukonUser(LiteYukonUser liteYukonUser) {
        this.liteYukonUser = liteYukonUser;
    }
    public Meter getMeter() {
        return meter;
    }
    public void setMeter(Meter meter) {
        this.meter = meter;
    }
}
