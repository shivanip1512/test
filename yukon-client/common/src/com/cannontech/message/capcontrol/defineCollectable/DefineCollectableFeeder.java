package com.cannontech.message.capcontrol.defineCollectable;

/**
 * Insert the type's description here.
 * Creation date: (8/17/00 3:21:47 PM)
 * @author: 
 */
import java.io.IOException;
import java.util.Date;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.util.CollectionExtracter;
import com.cannontech.message.util.CollectionInserter;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableFeeder extends DefineCollectableStreamableCapObject {
    
    //The roguewave class id
    private static int CTI_CCFEEDER_ID = 503;
    
    public DefineCollectableFeeder() {
        super();
    }
    
    public Object create(VirtualInputStream vstr) {
        return new Feeder();
    }
    
    public int getCxxClassId() {
        return CTI_CCFEEDER_ID;
    }
    
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }
    
    public Class<Feeder> getJavaClass() {
        return Feeder.class;
    }
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {
        super.restoreGuts( obj, vstr, polystr );
        
        Feeder feeder = (Feeder)obj;
        
        feeder.setMaxDailyOperation((int)vstr.extractUnsignedInt());
        feeder.setMaxOperationDisableFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
        
        feeder.setCurrentVarLoadPointID((int)vstr.extractUnsignedInt());
        feeder.setCurrentVarLoadPointValue(vstr.extractDouble());
        feeder.setCurrentWattLoadPointID((int)vstr.extractUnsignedInt());
        feeder.setCurrentWattLoadPointValue(vstr.extractDouble());
        feeder.setMapLocationID((String) vstr.restoreObject( SimpleMappings.CString));
       
        feeder.setDisplayOrder(vstr.extractFloat());
    
        feeder.setNewPointDataReceivedFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
    
        feeder.setLastCurrentVarPointUpdateTime((Date)vstr.restoreObject(SimpleMappings.Time));
        feeder.setEstimatedVarLoadPointID((int)vstr.extractUnsignedInt());
        feeder.setEstimatedVarLoadPointValue(vstr.extractDouble());
        
        feeder.setDailyOperationsAnalogPointID((int)vstr.extractUnsignedInt());
        feeder.setPowerFactorPointID((int)vstr.extractUnsignedInt());
        feeder.setEstimatedPowerFactorPointID((int)vstr.extractUnsignedInt());
        feeder.setCurrentDailyOperations((int)vstr.extractUnsignedInt());
        feeder.setRecentlyControlledFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
    
        feeder.setLastOperationTime((Date)vstr.restoreObject(SimpleMappings.Time));
        feeder.setVarValueBeforeControl(vstr.extractDouble());
    
        feeder.setPowerFactorValue(vstr.extractDouble());
    
        feeder.setEstimatedPFValue(vstr.extractDouble());
        feeder.setCurrentVarPtQuality((int)vstr.extractUnsignedInt());
    
        feeder.setWaiveControlFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
    
        feeder.setControlUnits(ControlAlgorithm.valueOf((String)vstr.restoreObject(SimpleMappings.CString)));
        feeder.setDecimalPlaces((int)vstr.extractUnsignedInt());
        feeder.setPeakTimeFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
    
        feeder.setPeakLag(vstr.extractDouble());
        feeder.setOffPkLag(vstr.extractDouble());
        feeder.setPeakLead(vstr.extractDouble());
        feeder.setOffPkLead(vstr.extractDouble());
        feeder.setCurrentVoltLoadPointID((int)vstr.extractUnsignedInt());
        feeder.setCurrentVoltLoadPointValue(vstr.extractDouble());
        feeder.setCurrentwattpointquality(vstr.extractInt());
        feeder.setCurrentvoltpointquality(vstr.extractInt());
        feeder.setTargetvarvalue(vstr.extractDouble());
        feeder.setSolution((String) vstr.restoreObject(SimpleMappings.CString));
        feeder.setOvUvDisabledFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
        feeder.setPeakPFSetPoint(vstr.extractDouble());
        feeder.setOffpeakPFSetPoint(vstr.extractDouble());
        feeder.setControlmethod(ControlMethod.valueOf((String) vstr.restoreObject( SimpleMappings.CString)));
        feeder.setPhaseA(vstr.extractDouble());
        feeder.setPhaseB(vstr.extractDouble());
        feeder.setPhaseC(vstr.extractDouble());
        feeder.setLikeDayControlFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
        feeder.setUsePhaseData((int)vstr.extractUnsignedInt() == 1 ? true : false);
        feeder.setOriginalParentId(vstr.extractInt());
        feeder.setCcCapBanks(CollectionExtracter.extractVector(vstr,polystr));
    }
    
    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException {
        super.saveGuts(obj, vstr, polystr);
    
        Feeder feeder = (Feeder)obj;
    
        vstr.insertUnsignedInt(feeder.getMaxDailyOperation());
        vstr.insertUnsignedInt(feeder.getMaxOperationDisableFlag() ? 1 : 0);
    
        vstr.insertUnsignedInt(feeder.getCurrentVarLoadPointID());
        vstr.insertDouble(feeder.getCurrentVarLoadPointValue());
        vstr.insertUnsignedInt(feeder.getCurrentWattLoadPointID());
        vstr.insertDouble(feeder.getCurrentWattLoadPointValue());
        vstr.saveObject(feeder.getMapLocationID(), SimpleMappings.CString);
       
        vstr.insertFloat( feeder.getDisplayOrder()); 
        vstr.insertUnsignedInt(feeder.getNewPointDataReceivedFlag() ? 1 : 0);
    
        vstr.saveObject(feeder.getLastCurrentVarPointUpdateTime(), SimpleMappings.Time);
        vstr.insertUnsignedInt(feeder.getEstimatedVarLoadPointID());
        vstr.insertDouble(feeder.getEstimatedVarLoadPointValue());
    
        vstr.insertUnsignedInt(feeder.getDailyOperationsAnalogPointID());    
        vstr.insertUnsignedInt(feeder.getPowerFactorPointID());
        vstr.insertUnsignedInt(feeder.getEstimatedPowerFactorPointID());    
        vstr.insertUnsignedInt(feeder.getCurrentDailyOperations());
        vstr.insertUnsignedInt(feeder.getRecentlyControlledFlag() ? 1 : 0);
    
        vstr.saveObject(feeder.getLastOperationTime(), SimpleMappings.Time);
        vstr.insertDouble(feeder.getVarValueBeforeControl());
    
       vstr.insertDouble(feeder.getPowerFactorValue());
    
       vstr.insertDouble(feeder.getEstimatedPFValue());
       vstr.insertUnsignedInt(feeder.getCurrentVarPtQuality());
    
        vstr.insertUnsignedInt(feeder.getWaiveControlFlag() ? 1 : 0);
    
        vstr.saveObject(feeder.getControlUnits(), SimpleMappings.CString);
        vstr.insertUnsignedInt(feeder.getDecimalPlaces());
        vstr.insertUnsignedInt(feeder.getPeakTimeFlag() ? 1 : 0);
    
        vstr.insertDouble(feeder.getPeakLag());
        vstr.insertDouble(feeder.getOffPkLag());
        vstr.insertDouble(feeder.getPeakLead());
        vstr.insertDouble(feeder.getOffPkLead());
        vstr.insertUnsignedInt(feeder.getCurrentVoltLoadPointID());
        vstr.insertDouble(feeder.getCurrentVoltLoadPointValue());
    
        vstr.insertInt(feeder.getCurrentwattpointquality());
        vstr.insertInt(feeder.getCurrentvoltpointquality());
        vstr.insertDouble(feeder.getTargetvarvalue());
        vstr.saveObject(feeder.getSolution(), SimpleMappings.CString);
        vstr.insertUnsignedInt(feeder.getOvUvDisabledFlag() ? 1 : 0);
        vstr.insertDouble(feeder.getPeakPFSetPoint());
        vstr.insertDouble(feeder.getOffpeakPFSetPoint());
        vstr.saveObject(feeder.getControlmethod(), SimpleMappings.CString);
        
        vstr.insertDouble(feeder.getPhaseA());
        vstr.insertDouble(feeder.getPhaseB());
        vstr.insertDouble(feeder.getPhaseC());
        vstr.insertUnsignedInt(feeder.getLikeDayControlFlag() ? 1 : 0);
        vstr.insertUnsignedInt(feeder.getUsePhaseData() ? 1 : 0);
        vstr.insertInt(feeder.getOriginalParentId());
        CollectionInserter.insertVector(feeder.getCcCapBanks(), vstr, polystr);
    }
    
}