package com.cannontech.message.capcontrol.defineCollectable;

import java.io.IOException;

import com.cannontech.message.capcontrol.streamable.SpecialArea;
import com.cannontech.message.util.CollectionExtracter;
import com.cannontech.message.util.CollectionInserter;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableSpecialArea extends DefineCollectableStreamableCapObject {
    // The roguewave class id
    private static int CTI_CCSPECIALAREA_ID = 523;

    public DefineCollectableSpecialArea() {
        super();
    }

    @Override
    public Object create(VirtualInputStream vstr) {
        return new SpecialArea();
    }

    @Override
    public int getCxxClassId() {
        return CTI_CCSPECIALAREA_ID;
    }

    @Override
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }

    @Override
    public Class<SpecialArea> getJavaClass() {
        return SpecialArea.class;
    }

    @Override
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {
        SpecialArea area = (SpecialArea) obj;

        int paoId = (int) vstr.extractUnsignedInt();
        area.setPaoId(paoId);
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
        
        Boolean disableFlag = (int) vstr.extractUnsignedInt() == 1 ? true : false;
        area.setDisableFlag(disableFlag);
        area.setCcDisableFlag(disableFlag);
        area.setCcSubIds(CollectionExtracter.extractIntArray(vstr, polystr));
        area.setOvUvDisabledFlag((int) vstr.extractUnsignedInt() == 1 ? true : false);
        area.setPowerFactorValue(vstr.extractDouble());
        area.setEstimatedPFValue(vstr.extractDouble());
        area.setVoltReductionFlag((int) vstr.extractUnsignedInt() == 1 ? true : false);
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException {
        SpecialArea area = (SpecialArea) obj;
        
        vstr.insertUnsignedInt(area.getPaoId());
        vstr.saveObject(area.getPaoCategory(), SimpleMappings.CString);
        vstr.saveObject(area.getPaoClass(), SimpleMappings.CString);
        vstr.saveObject(area.getPaoName(), SimpleMappings.CString);
        vstr.saveObject(area.getPaoType(), SimpleMappings.CString);
        vstr.saveObject(area.getPaoDescription(), SimpleMappings.CString);
        vstr.insertUnsignedInt(area.getDisableFlag() ? 1 : 0);
        CollectionInserter.insertIntArray(area.getCcSubIds(), vstr, polystr);
        vstr.insertUnsignedInt(area.getOvUvDisabledFlag() ? 1 : 0);
        vstr.insertDouble(area.getPowerFactorValue());
        vstr.insertDouble( area.getEstimatedPFValue());
        vstr.insertUnsignedInt(area.getVoltReductionFlag() ? 1 : 0);
    }
    
}