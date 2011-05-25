package com.cannontech.yukon.cbc;

import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.message.util.CollectionExtracter;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefineCollectableVoltageRegulatorMessage extends DefineCollectableCBCMessage {

    //Id for RogueWave. Must match C++
    public static final int CTI_VOLTAGE_REGULATOR_MESSAGE_ID = 528;

    public DefineCollectableVoltageRegulatorMessage() {
        super();
    }

    public Object create(com.roguewave.vsj.VirtualInputStream vstr) {
        return new VoltageRegulatorFlagMessage();
    }

    public com.roguewave.tools.v2_0.Comparator getComparator() {
        return new Comparator() {
            public int compare(Object x, Object y) {
                if (x == y)
                    return 0;
                else
                    return -1;
            }
        };
    }

    public int getCxxClassId() {
        return CTI_VOLTAGE_REGULATOR_MESSAGE_ID;
    }

    public Class getJavaClass() {
        return VoltageRegulatorFlagMessage.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr)
    throws java.io.IOException {

        super.restoreGuts(obj, vstr, polystr);
        Vector<VoltageRegulatorFlags> voltageRegulatorFlags = CollectionExtracter.extractVector(vstr, polystr);
        
        ((VoltageRegulatorFlagMessage) obj).setVoltageRegulatorFlags(voltageRegulatorFlags);
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) {
        CTILogger.info("**********************************************************************************");
        CTILogger.info("com.cannontech.cbc.DefineCollectableLtcMessage.saveGuts() should not be called");
        CTILogger.info("**********************************************************************************");
    }
    
}
