package com.cannontech.message.capcontrol.defineCollectable;

import java.io.IOException;

import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.message.util.CollectionExtracter;
import com.cannontech.message.util.CollectionInserter;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefineCollectableSubStation extends DefineCollectableStreamableCapObject {

    private static int CTI_CCSUBSTATION_ID = 524;
    
    public DefineCollectableSubStation() {
        super();
    }
    
    public Object create(VirtualInputStream vstr) {
        return new SubStation();
    }
    
    public int getCxxClassId() {
        return CTI_CCSUBSTATION_ID;
    }
    
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }
    
    public Class<SubStation> getJavaClass() {
        return SubStation.class;
    }
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {
        super.restoreGuts( obj, vstr, polystr );
        SubStation sub = (SubStation) obj;

        sub.setOvuvDisableFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
        int[] ids = CollectionExtracter.extractIntArray(vstr, polystr);
        sub.setSubBusIds(ids);
        sub.setPowerFactorValue(vstr.extractDouble());
        sub.setEstimatedPFValue(vstr.extractDouble());
        sub.setSpecialAreaEnabled((int)vstr.extractUnsignedInt() == 1 ? true : false);
        sub.setSpecialAreaId(vstr.extractInt());
        sub.setVoltReductionFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
        sub.setRecentlyControlledFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
        sub.setChildVoltReductionFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
    }
    
    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException {
        super.saveGuts( obj, vstr, polystr );
        SubStation sub = (SubStation)obj;
        
        vstr.insertUnsignedInt(sub.getOvuvDisableFlag() ? 1 : 0);
        CollectionInserter.insertIntArray(sub.getSubBusIds(), vstr, polystr);
        vstr.insertDouble(sub.getPowerFactorValue());
        vstr.insertDouble(sub.getEstimatedPFValue());
        vstr.insertUnsignedInt(sub.getSpecialAreaEnabled() ? 1 : 0);
        vstr.insertUnsignedInt(sub.getSpecialAreaId());
        vstr.insertUnsignedInt(sub.getVoltReductionFlag() ? 1 : 0);
        vstr.insertUnsignedInt(sub.getRecentlyControlledFlag() ? 1 : 0);
        vstr.insertUnsignedInt(sub.getChildVoltReductionFlag() ? 1 : 0);
    }
    
}