package com.cannontech.messaging.message.capcontrol;

import java.util.Vector;

import com.cannontech.messaging.message.capcontrol.streamable.VoltageRegulatorFlags;

public class VoltageRegulatorFlagMessage extends CapControlMessage {

    private Vector<VoltageRegulatorFlags> voltageRegulatorFlags;

    public void setVoltageRegulatorFlags(Vector<VoltageRegulatorFlags> voltageRegulatorFlags) {
        this.voltageRegulatorFlags = voltageRegulatorFlags;
    }

    public Vector<VoltageRegulatorFlags> getVoltageRegulatorFlags() {
        return voltageRegulatorFlags;
    }
}
