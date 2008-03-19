package com.cannontech.yukon.cbc;

/**
 * This type was created in VisualAge.
 */
import java.util.Vector;

import com.cannontech.message.util.VectorExtract;
import com.roguewave.tools.v2_0.Comparator;

public class DefineCollectableCCSubAreas extends DefineCollectableCBCMessage {
    // RogueWave classId
    public static final int CTIAREA_MESSAGE_ID = 509;

    /**
     * DefineCollectableMessage constructor comment.
     */
    public DefineCollectableCCSubAreas() {
        super();
    }

    /**
     * create method comment.
     */
    public Object create(com.roguewave.vsj.VirtualInputStream vstr)
            throws java.io.IOException {
        return new CCSubAreas();
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
        return CTIAREA_MESSAGE_ID;
    }

    /**
     * getJavaClass method comment.
     */
    public Class getJavaClass() {
        return CCSubAreas.class;
    }

    /**
     * restoreGuts method comment.
     */
    public void restoreGuts(Object obj,
            com.roguewave.vsj.VirtualInputStream vstr,
            com.roguewave.vsj.CollectableStreamer polystr)
            throws java.io.IOException {

        super.restoreGuts(obj, vstr, polystr);

        Vector areaNames = VectorExtract.extractVector(vstr, polystr);

        ((CCSubAreas) obj).setAreas(areaNames);
    }

    /**
     * saveGuts method comment.
     */
    public void saveGuts(Object obj,
            com.roguewave.vsj.VirtualOutputStream vstr,
            com.roguewave.vsj.CollectableStreamer polystr)
            throws java.io.IOException {
        com.cannontech.clientutils.CTILogger.info("**********************************************************************************");
        com.cannontech.clientutils.CTILogger.info("com.cannontech.cbc.DefineCollectableCBCStrategyAreaMessage.saveGuts() should not be called");
        com.cannontech.clientutils.CTILogger.info("**********************************************************************************");

    }
}
