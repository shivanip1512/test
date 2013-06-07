package com.cannontech.message.capcontrol;

import java.io.IOException;
import java.util.Vector;

import com.cannontech.message.util.CollectionExtracter;
import com.cannontech.messaging.message.capcontrol.SpecialAreasMessage;
import com.cannontech.messaging.message.capcontrol.streamable.SpecialArea;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefineCollectableSpecialAreas extends DefineCollectableCapControlMessage {
    
    // RogueWave classId
    //TODO get a real id
    public static final int CTISPECIAL_AREA_MESSAGE_ID = 522;

    public DefineCollectableSpecialAreas() {
        super();
    }

    public Object create(VirtualInputStream vstr) {
        return new SpecialAreasMessage();
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
        return CTISPECIAL_AREA_MESSAGE_ID;
    }

    public Class<SpecialAreasMessage> getJavaClass() {
        return SpecialAreasMessage.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        Vector<SpecialArea> areaNames = CollectionExtracter.extractVector(vstr, polystr);
        ((SpecialAreasMessage) obj).setAreas(areaNames);
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws java.io.IOException {
        throw new UnsupportedOperationException("SaveGuts invalid for " + this.getClass().getName());
    }
    
}