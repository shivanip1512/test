package com.cannontech.message.capcontrol.defineCollectable;

import java.io.IOException;
import java.util.Vector;

import com.cannontech.message.capcontrol.model.VoltageRegulatorFlagMessage;
import com.cannontech.message.capcontrol.streamable.VoltageRegulatorFlags;
import com.cannontech.message.util.CollectionExtracter;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefineCollectableVoltageRegulatorMessage extends DefineCollectableCapControlMessage {

    //Id for RogueWave. Must match C++
    public static final int CTI_VOLTAGE_REGULATOR_MESSAGE_ID = 528;

    public DefineCollectableVoltageRegulatorMessage() {
        super();
    }

    public Object create(VirtualInputStream vstr) {
        return new VoltageRegulatorFlagMessage();
    }

    public Comparator getComparator() {
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

    public Class<VoltageRegulatorFlagMessage> getJavaClass() {
        return VoltageRegulatorFlagMessage.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        Vector<VoltageRegulatorFlags> voltageRegulatorFlags = CollectionExtracter.extractVector(vstr, polystr);
        
        ((VoltageRegulatorFlagMessage) obj).setVoltageRegulatorFlags(voltageRegulatorFlags);
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) {
        throw new UnsupportedOperationException("SaveGuts invalid for " + this.getClass().getName());
    }
    
}