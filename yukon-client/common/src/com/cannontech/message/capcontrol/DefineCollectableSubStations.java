package com.cannontech.message.capcontrol;

import java.io.IOException;
import java.util.Vector;

import com.cannontech.message.util.CollectionExtracter;
import com.cannontech.messaging.message.capcontrol.SubStationsMessage;
import com.cannontech.messaging.message.capcontrol.streamable.SubStation;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefineCollectableSubStations extends DefineCollectableCapControlMessage {
    
    public static final int CTI_CCSUBSTATION_MSG_ID = 525;

    public DefineCollectableSubStations() {
        super();
    }

    public Object create(VirtualInputStream vstr) {
        return new SubStationsMessage();
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
        return CTI_CCSUBSTATION_MSG_ID;
    }

    public Class<SubStationsMessage> getJavaClass() {
        return SubStationsMessage.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {
        super.restoreGuts( obj, vstr, polystr );

        SubStationsMessage cbcSub = (SubStationsMessage) obj;
        cbcSub.setMsgInfoBitMask((int)vstr.extractUnsignedInt());
        Vector<SubStation> stations = CollectionExtracter.extractVector(vstr,polystr);

        cbcSub.setSubStations(stations);
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) {
        throw new UnsupportedOperationException("SaveGuts invalid for " + this.getClass().getName());
    }
    
}