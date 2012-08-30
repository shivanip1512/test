package com.cannontech.loadcontrol.loadgroup.model;

public class SEPGroupAttributes {
    private byte utilityEnrollmentGroup;
    private int rampIn;
    private int rampOut;
    
    public SEPGroupAttributes(byte utilityEnrollmentGroup, int rampIn, int rampOut) {
        this.utilityEnrollmentGroup = utilityEnrollmentGroup;
        this.rampIn = rampIn;
        this.rampOut = rampOut;
    }

    public byte getUtilityEnrollmentGroup() {
        return utilityEnrollmentGroup;
    }

    public int getRampIn() {
        return rampIn;
    }

    public int getRampOut() {
        return rampOut;
    }
    
}
