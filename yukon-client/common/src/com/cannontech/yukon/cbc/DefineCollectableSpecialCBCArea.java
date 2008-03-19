package com.cannontech.yukon.cbc;

import com.cannontech.message.util.VectorExtract;
import com.cannontech.message.util.VectorInsert;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableSpecialCBCArea extends DefineCollectableStreamableCapObject {
    // The roguewave class id
    private static int CTI_CCSPECIALAREA_ID = 523;

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
    @Override
    public Object create(com.roguewave.vsj.VirtualInputStream vstr)
            throws java.io.IOException {
        return new CCSpecialArea();
    }

    /**
     * getCxxClassId method comment.
     */
    @Override
    public int getCxxClassId() {
        return CTI_CCSPECIALAREA_ID;
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
    public Class<CCSpecialArea> getJavaClass() {
        return CCSpecialArea.class;
    }

    /**
     * restoreGuts method comment.
     */

    @Override
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws java.io.IOException {
        
        CCSpecialArea area = (CCSpecialArea) obj;

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
        area.setCcSubIds( VectorExtract.extractIntArray(vstr, polystr));
        area.setOvUvDisabledFlag(((int) vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false));
        area.setPowerFactorValue( new Double( vstr.extractDouble() ) );
        area.setEstimatedPFValue( new Double( vstr.extractDouble() ) );
        area.setVoltReductionFlag(((int) vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false));
        
    }

    /**
     * saveGuts method comment.
     */
    @Override
    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws java.io.IOException {
    	CCSpecialArea area = (CCSpecialArea) obj;
        
        vstr.insertUnsignedInt(area.getPaoID().intValue());
        vstr.saveObject(area.getPaoCategory(), SimpleMappings.CString);
        vstr.saveObject(area.getPaoClass(), SimpleMappings.CString);
        vstr.saveObject(area.getPaoName(), SimpleMappings.CString);
        vstr.saveObject(area.getPaoType(), SimpleMappings.CString);
        vstr.saveObject(area.getPaoDescription(), SimpleMappings.CString);
        vstr.insertUnsignedInt((area.getDisableFlag().booleanValue()) ? 1 : 0);
        VectorInsert.insertIntArray(area.getCcSubIds(), vstr, polystr);
        vstr.insertUnsignedInt((area.getOvUvDisabledFlag().booleanValue()) ? 1 : 0);
        vstr.insertDouble( area.getPowerFactorValue().doubleValue() );
        vstr.insertDouble( area.getEstimatedPFValue().doubleValue() );
        vstr.insertUnsignedInt((area.getVoltReductionFlag().booleanValue()) ? 1 : 0);
        
    }
}

