package com.cannontech.stars.xml.serialize;

import java.util.Date;

public abstract class StarsCallRprt {
    private int callID;
    private boolean hasCallID;
    private String callNumber;
    private Date callDate;
    private CallType callType;
    private String takenBy;
    private String description;

    public StarsCallRprt() {
        
    }

    public void deleteCallID() {
        this.hasCallID = false;
    } 

    public Date getCallDate() {
        return this.callDate;
    } 

    public int getCallID() {
        return this.callID;
    }

    public String getCallNumber() {
        return this.callNumber;
    } 

    public CallType getCallType() {
        return this.callType;
    }

    public String getDescription() {
        return this.description;
    } 

    public String getTakenBy() {
        return this.takenBy;
    } 

    public boolean hasCallID() {
        return this.hasCallID;
    }

    public void setCallDate(Date callDate) {
        this.callDate = callDate;
    } 

    public void setCallID(int callID) {
        this.callID = callID;
        this.hasCallID = true;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    } 

    public void setCallType(CallType callType) {
        this.callType = callType;
    } 

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTakenBy(String takenBy) {
        this.takenBy = takenBy;
    } 

}
