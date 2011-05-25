package com.cannontech.yukon.cbc;

import java.util.Vector;

public class VoltageRegulatorFlagMessage extends CapControlMessage {
    
    private Vector<VoltageRegulatorFlags> voltageRegulatorFlags;
    
    public VoltageRegulatorFlagMessage() {
        
    }
    
    public void setVoltageRegulatorFlags(Vector<VoltageRegulatorFlags> voltageRegulatorFlags) {
        this.voltageRegulatorFlags = voltageRegulatorFlags;
    }
    
    public Vector<VoltageRegulatorFlags> getVoltageRegulatorFlags() {
        return voltageRegulatorFlags;
    }
}
