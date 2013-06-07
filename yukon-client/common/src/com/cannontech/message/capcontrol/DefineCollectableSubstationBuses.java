package com.cannontech.message.capcontrol;

/**
 * This type was created in VisualAge.
 */
import java.io.IOException;
import java.util.Vector;

import com.cannontech.message.util.CollectionExtracter;
import com.cannontech.messaging.message.capcontrol.SubstationBusesMessage;
import com.cannontech.messaging.message.capcontrol.streamable.SubBus;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefineCollectableSubstationBuses extends DefineCollectableCapControlMessage {
    //RogueWave classId
    public static final int CTI_CCSUBSTATIONBUS_MSG_ID = 501;

    public DefineCollectableSubstationBuses() {
        super();
    }
    
    public Object create(VirtualInputStream vstr) {
        return new SubstationBusesMessage();
    }
    
    public Comparator getComparator() {
        return new Comparator() {
          public int compare(Object x, Object y) {
                if( x == y )
                    return 0;
                else
                    return -1;
          }
        };
    }
    
    public int getCxxClassId() {
        return CTI_CCSUBSTATIONBUS_MSG_ID;
    }
    
    public Class<SubstationBusesMessage> getJavaClass() {
        return SubstationBusesMessage.class;
    }
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {
        super.restoreGuts( obj, vstr, polystr );
    
        SubstationBusesMessage cbcSubBuses = (SubstationBusesMessage) obj;
    
        cbcSubBuses.setMsgInfoBitMask((int)vstr.extractUnsignedInt());
        
        Vector<SubBus> buses = CollectionExtracter.extractVector(vstr,polystr);
        cbcSubBuses.setSubBuses(buses);
    }
    
    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException {
        throw new UnsupportedOperationException("SaveGuts invalid for " + this.getClass().getName());
    }

}