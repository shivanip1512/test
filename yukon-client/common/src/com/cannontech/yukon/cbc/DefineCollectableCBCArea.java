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
    @Override
    public Object create(com.roguewave.vsj.VirtualInputStream vstr)
            throws java.io.IOException {
        return new CCArea();
    }

    /**
     * getCxxClassId method comment.
     */
    @Override
    public int getCxxClassId() {
        return CTI_CCAREA_ID;
    }

    /**
     * getCxxStringId method comment.
     */
    @Override
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }

    /**
     * getJavaClass method comment.
     */
    @Override
    public Class<CCArea> getJavaClass() {
        return CCArea.class;
    }

    /**
     * restoreGuts method comment.
     */

    @Override
    public void restoreGuts(Object obj,
            com.roguewave.vsj.VirtualInputStream vstr,
            com.roguewave.vsj.CollectableStreamer polystr)
            throws java.io.IOException {
        
        CCArea area = (CCArea) obj;

        int paoId = (int) vstr.extractUnsignedInt();
        area.setPaoID(paoId);
        area.setCcId(paoId);
        
        String newCcCategory = (String) vstr.restoreObject(SimpleMappings.CString);
        area.setPaoCategory(newCcCategory);
        area.setCcCategory(newCcCategory);
        
        String newClass = (String) vstr.restoreObject(SimpleMappings.CString);
        area.setPaoClass(newClass);
        area.setCcClass(newClass);
        
        String name = (String) vstr.restoreObject(SimpleMappings.CString);
        area.setPaoName(name);
        area.setCcName(name);
        
        String paoType = (String) vstr.restoreObject(SimpleMappings.CString);
        area.setPaoType(paoType);
        area.setCcType(paoType);
        
        String paoDescription = (String) vstr.restoreObject(SimpleMappings.CString);
        area.setPaoDescription(paoDescription);
        
        Boolean disableFlag = ((int) vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false);
        area.setDisableFlag(disableFlag);
        area.setCcDisableFlag(disableFlag);
        area.setOvUvDisabledFlag(((int) vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false));
        area.setStations( VectorExtract.extractIntArray(vstr, polystr));
        area.setPowerFactorValue( new Double( vstr.extractDouble() ) );
        area.setEstimatedPFValue( new Double( vstr.extractDouble() ) );
        area.setVoltReductionFlag(((int) vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false));
        area.setChildVoltReductionFlag(((int) vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false));
        
    }

    /**
     * saveGuts method comment.
     */
    @Override
    public void saveGuts(Object obj,
            com.roguewave.vsj.VirtualOutputStream vstr,
            com.roguewave.vsj.CollectableStreamer polystr)
            throws java.io.IOException {
        //super.saveGuts(obj, vstr, polystr);

        CCArea area = (CCArea) obj;
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
        vstr.insertUnsignedInt((area.getVoltReductionFlag().booleanValue()) ? 1 : 0);
        vstr.insertUnsignedInt((area.getChildVoltReductionFlag().booleanValue()) ? 1 : 0);
        
    }
}
