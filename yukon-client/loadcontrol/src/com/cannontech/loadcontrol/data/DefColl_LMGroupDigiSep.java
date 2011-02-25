package com.cannontech.loadcontrol.data;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.vsj.DefineCollectable;

public class DefColl_LMGroupDigiSep extends DefColl_LMDirectGroupBase {
    // The roguewave class id
    private static int CTILMGROUPDIGISEP_ID = 640;

    public DefColl_LMGroupDigiSep() {
        super();
    }

    public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
        return new LMGroupDigiSep();
    }

    public int getCxxClassId() {
        return CTILMGROUPDIGISEP_ID;
    }

    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }

    public Class getJavaClass() {
        return LMGroupDigiSep.class;
    }

    public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr,
                            com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
        super.restoreGuts(obj, vstr, polystr);
    }

    public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr,
                         com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
        super.saveGuts(obj, vstr, polystr);
    }
}
