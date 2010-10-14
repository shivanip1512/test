package com.cannontech.yukon.cbc;

import java.util.Vector;

public class VoltageRegulatorFlagMessage extends CapControlMessage {
    
    private Vector<VoltageRegulatorFlags> voltageRegulators;
    
    public VoltageRegulatorFlagMessage() {
        
    }
    
    public void setVoltageRegulators(Vector<VoltageRegulatorFlags> voltageRegulators) {
        this.voltageRegulators = voltageRegulators;
    }
    
    public Vector<VoltageRegulatorFlags> getVoltageRegulators() {
        return voltageRegulators;
    }
}
