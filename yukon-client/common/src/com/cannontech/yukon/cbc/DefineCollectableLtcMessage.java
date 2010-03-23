package com.cannontech.yukon.cbc;

import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.message.util.VectorExtract;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefineCollectableLtcMessage extends DefineCollectableCBCMessage {

    //Id for RogueWave. Must match C++
    public static final int CTILTC_MESSAGE_ID = 528;

    public DefineCollectableLtcMessage() {
        super();
    }

    public Object create(com.roguewave.vsj.VirtualInputStream vstr) {
        return new LtcMessage();
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
        return CTILTC_MESSAGE_ID;
    }

    public Class getJavaClass() {
        return LtcMessage.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr)
    throws java.io.IOException {

        super.restoreGuts(obj, vstr, polystr);
        Vector<Ltc> ltcs = VectorExtract.extractVector(vstr, polystr);
        
        ((LtcMessage) obj).setLtcs(ltcs);
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) {
        CTILogger.info("**********************************************************************************");
        CTILogger.info("com.cannontech.cbc.DefineCollectableLtcMessage.saveGuts() should not be called");
        CTILogger.info("**********************************************************************************");
    }
    
}
