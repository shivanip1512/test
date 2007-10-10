package com.cannontech.yukon.cbc;

import com.cannontech.message.util.VectorExtract;
import com.cannontech.message.util.VectorInsert;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableCBCArea extends
        DefineCollectableStreamableCapObject {
    // The roguewave class id
    private static int CTI_CCAREA_ID = 520;

    /**
     * DefineCollectableCapBankVector constructor comment.
     */
    public DefineCollectableCBCArea() {
        super();
    }

    /**
     * This method is called from CollectableStreamer to create a new instance
     * of Schedule.
     */
    public Object create(com.roguewave.vsj.VirtualInputStream vstr)
            throws java.io.IOException {
        return new CBCArea();
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
        return CBCArea.class;
    }

    /**
     * restoreGuts method comment.
     */

    public void restoreGuts(Object obj,
            com.roguewave.vsj.VirtualInputStream vstr,
            com.roguewave.vsj.CollectableStreamer polystr)
            throws java.io.IOException {
        //super.restoreGuts(obj, vstr, polystr);

        CBCArea area = (CBCArea) obj;

        area.setPaoID(new Integer((int) vstr.extractUnsignedInt()));
        area.setPaoCategory((String) vstr.restoreObject(SimpleMappings.CString));
        area.setPaoClass((String) vstr.restoreObject(SimpleMappings.CString));
        area.setPaoName((String) vstr.restoreObject(SimpleMappings.CString));
        area.setPaoType((String) vstr.restoreObject(SimpleMappings.CString));
        area.setPaoDescription((String) vstr.restoreObject(SimpleMappings.CString));
        area.setDisableFlag(((int) vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false));
        area.setOvUvDisabledFlag(((int) vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false));
        area.setStations( VectorExtract.extractIntArray(vstr, polystr));
        area.setPowerFactorValue( new Double( vstr.extractDouble() ) );
        area.setEstimatedPFValue( new Double( vstr.extractDouble() ) );
    }

    /**
     * saveGuts method comment.
     */
    public void saveGuts(Object obj,
            com.roguewave.vsj.VirtualOutputStream vstr,
            com.roguewave.vsj.CollectableStreamer polystr)
            throws java.io.IOException {
        //super.saveGuts(obj, vstr, polystr);

        CBCArea area = (CBCArea) obj;
        vstr.insertUnsignedInt(area.getPaoID().intValue());
        vstr.saveObject(area.getPaoCategory(), SimpleMappings.CString);
        vstr.saveObject(area.getPaoClass(), SimpleMappings.CString);
        vstr.saveObject(area.getPaoName(), SimpleMappings.CString);
        vstr.saveObject(area.getPaoType(), SimpleMappings.CString);
        vstr.saveObject(area.getPaoDescription(), SimpleMappings.CString);
        vstr.insertUnsignedInt((area.getDisableFlag().booleanValue()) ? 1 : 0);
        vstr.insertUnsignedInt((area.getOvUvDisabledFlag().booleanValue()) ? 1 : 0);
        VectorInsert.insertIntArray(area.getStations(), vstr, polystr);
        vstr.insertDouble( area.getPowerFactorValue().doubleValue() );
        vstr.insertDouble( area.getEstimatedPFValue().doubleValue() );
    }
}
