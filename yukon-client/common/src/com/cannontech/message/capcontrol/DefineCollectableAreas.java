package com.cannontech.message.capcontrol;

import java.io.IOException;
import java.util.Vector;

import com.cannontech.message.util.CollectionExtracter;
import com.cannontech.messaging.message.capcontrol.SubAreasMessage;
import com.cannontech.messaging.message.capcontrol.streamable.Area;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefineCollectableAreas extends DefineCollectableCapControlMessage {
    
    // RogueWave classId
    public static final int CTIAREA_MESSAGE_ID = 509;

    public DefineCollectableAreas() {
        super();
    }

    public Object create(VirtualInputStream vstr) {
        return new SubAreasMessage();
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
        return CTIAREA_MESSAGE_ID;
    }

    public Class<SubAreasMessage> getJavaClass() {
        return SubAreasMessage.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        SubAreasMessage areas = (SubAreasMessage) obj;
        
        areas.setMsgInfoBitMask((int)vstr.extractUnsignedInt());
        Vector<Area> areaNames = CollectionExtracter.extractVector(vstr, polystr);
        areas.setAreas(areaNames);
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) {
        throw new UnsupportedOperationException("SaveGuts invalid for " + this.getClass().getName());
    }
    
}