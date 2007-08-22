package com.cannontech.yukon.cbc;

import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableSpecialCBCArea extends DefineCollectableStreamableCapObject {
    // The roguewave class id
    private static int CTI_CCAREA_ID = 523;

    /**
     * DefineCollectableCapBankVector constructor comment.
     */
    public DefineCollectableSpecialCBCArea() {
        super();
    }

    /**
     * This method is called from CollectableStreamer to create a new instance
     * of Schedule.
     */
    public Object create(com.roguewave.vsj.VirtualInputStream vstr)
            throws java.io.IOException {
        return new CBCSpecialArea();
    }

    /**
     * getCxxClassId method comment.
     */
    public int getCxxClassId() {
        return CTI_CCAREA_ID;
    }

    /**
     * getCxxStringId method comment.
     */
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }

    /**
     * getJavaClass method comment.
     */
    public Class getJavaClass() {
        return CBCSpecialArea.class;
    }

    /**
     * restoreGuts method comment.
     */

    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws java.io.IOException {
        CBCSpecialArea area = (CBCSpecialArea) obj;

        area.setPaoID(new Integer((int) vstr.extractUnsignedInt()));
        area.setPaoCategory((String) vstr.restoreObject(SimpleMappings.CString));
        area.setPaoClass((String) vstr.restoreObject(SimpleMappings.CString));
        area.setPaoName((String) vstr.restoreObject(SimpleMappings.CString));
        area.setPaoType((String) vstr.restoreObject(SimpleMappings.CString));
        area.setPaoDescription((String) vstr.restoreObject(SimpleMappings.CString));
        area.setDisableFlag(((int) vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false));
        area.setOvUvDisabledFlag(((int) vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false));

    }

    /**
     * saveGuts method comment.
     */
    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws java.io.IOException {
        CBCArea area = (CBCArea) obj;
        
        vstr.insertUnsignedInt(area.getPaoID().intValue());
        vstr.saveObject(area.getPaoCategory(), SimpleMappings.CString);
        vstr.saveObject(area.getPaoClass(), SimpleMappings.CString);
        vstr.saveObject(area.getPaoName(), SimpleMappings.CString);
        vstr.saveObject(area.getPaoType(), SimpleMappings.CString);
        vstr.saveObject(area.getPaoDescription(), SimpleMappings.CString);
        vstr.insertUnsignedInt((area.getDisableFlag().booleanValue()) ? 1 : 0);
        vstr.insertUnsignedInt((area.getOvUvDisabledFlag().booleanValue()) ? 1 : 0);

    }
}

