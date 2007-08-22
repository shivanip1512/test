package com.cannontech.yukon.cbc;

/**
 * This type was created in VisualAge.
 */
import java.util.Vector;

import com.cannontech.message.util.VectorExtract;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefineCollectableCBCSpecialSubAreas extends DefineCollectableCBCMessage {
    // RogueWave classId
    //TODO get a real id
    public static final int CTISPECIAL_AREA_MESSAGE_ID = 522;

    /**
     * DefineCollectableMessage constructor comment.
     */
    public DefineCollectableCBCSpecialSubAreas() {
        super();
    }

    /**
     * create method comment.
     */
    public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
        return new CBCSubSpecialAreas();
    }

    /**
     * getComparator method comment.
     */
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

    /**
     * getCxxClassId method comment.
     */
    public int getCxxClassId() {
        return CTISPECIAL_AREA_MESSAGE_ID;
    }

    /**
     * getJavaClass method comment.
     */
    public Class getJavaClass() {
        return CBCSubSpecialAreas.class;
    }

    /**
     * restoreGuts method comment.
     */
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws java.io.IOException {

        super.restoreGuts(obj, vstr, polystr);
        Vector areaNames = VectorExtract.extractVector(vstr, polystr);
        ((CBCSubSpecialAreas) obj).setAreas(areaNames);
    }

    /**
     * saveGuts method comment.
     */
    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws java.io.IOException {
        com.cannontech.clientutils.CTILogger.info("**********************************************************************************");
        com.cannontech.clientutils.CTILogger.info("com.cannontech.cbc.DefineCollectableCBCSpecialSubAreas.saveGuts() should not be called");
        com.cannontech.clientutils.CTILogger.info("**********************************************************************************");

    }
}

