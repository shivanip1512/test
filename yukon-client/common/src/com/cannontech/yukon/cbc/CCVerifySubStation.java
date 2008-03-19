package com.cannontech.yukon.cbc;

public class CCVerifySubStation extends CapControlMessage {
    public static final long DEFAULT_CB_INACT_TIME = -1;
    
    int action; //enable or disable...
    int subId; //subStationId
    int strategy;
    long cbInactivityTime;
    
    public CCVerifySubStation() {
        super();
    }

    public CCVerifySubStation (int _action_, int _subId_, int _strategy_, long _cbInactivityTime_){
        action = _action_;
        subId = _subId_;
        strategy = _strategy_;
        cbInactivityTime = _cbInactivityTime_;      
    }
    
    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public long getCbInactivityTime() {
        return cbInactivityTime;
    }

    public void setCbInactivityTime(long cbInactivityTime) {
        this.cbInactivityTime = cbInactivityTime;
    }

    public int getStrategy() {
        return strategy;
    }

    public void setStrategy(int strategy) {
        this.strategy = strategy;
    }

    public int getSubId() {
        return subId;
    }

    public void setSubId(int sub_id) {
        this.subId = sub_id;
    }

}