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
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.util.CollectionExtracter;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableSubBus extends DefineCollectableStreamableCapObject {
    //The roguewave class id
    private static int CTI_CCSUBSTATIONBUS_ID = 502;
    
    public DefineCollectableSubBus() {
        super();
    }
    
    public Object create(VirtualInputStream vstr) throws IOException {
        return new SubBus();
    }
    
    public int getCxxClassId() {
        return CTI_CCSUBSTATIONBUS_ID;
    }
    
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }
    
    public Class<SubBus> getJavaClass() {
        return SubBus.class;
    }
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {
        super.restoreGuts( obj, vstr, polystr );
        
        SubBus subBus = (SubBus) obj;
        
        subBus.setMaxDailyOperation((int)vstr.extractUnsignedInt());
        subBus.setMaxOperationDisableFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
        subBus.setCurrentVarLoadPointID((int)vstr.extractUnsignedInt());
        subBus.setCurrentVarLoadPointValue(vstr.extractDouble());
        subBus.setCurrentWattLoadPointID((int)vstr.extractUnsignedInt());
        subBus.setCurrentWattLoadPointValue(vstr.extractDouble());
        subBus.setMapLocationID((String) vstr.restoreObject(SimpleMappings.CString));
        subBus.setControlUnits(ControlAlgorithm.valueOf((String) vstr.restoreObject(SimpleMappings.CString)));
        subBus.setDecimalPlaces((int)vstr.extractUnsignedInt());
        subBus.setNewPointDataReceivedFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
        subBus.setBusUpdateFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
        subBus.setLastCurrentVarPointUpdateTime((Date)vstr.restoreObject(SimpleMappings.Time));
        subBus.setEstimatedVarLoadPointID((int)vstr.extractUnsignedInt());
        subBus.setEstimatedVarLoadPointValue(vstr.extractDouble());
        subBus.setDailyOperationsAnalogPointId((int)vstr.extractUnsignedInt());
        subBus.setPowerFactorPointId((int)vstr.extractUnsignedInt());
        subBus.setEstimatedPowerFactorPointId((int)vstr.extractUnsignedInt());
        subBus.setCurrentDailyOperations((int)vstr.extractUnsignedInt());
        subBus.setPeakTimeFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
        subBus.setRecentlyControlledFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
        subBus.setLastOperationTime((Date)vstr.restoreObject(SimpleMappings.Time));
        subBus.setVarValueBeforeControl(vstr.extractDouble());
        subBus.setPowerFactorValue(vstr.extractDouble());
        subBus.setEstimatedPFValue(vstr.extractDouble());
        subBus.setCurrentVarPtQuality((int)vstr.extractUnsignedInt());
        subBus.setWaiveControlFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
        subBus.setPeakLag(vstr.extractDouble());
        subBus.setOffPkLag(vstr.extractDouble());
        subBus.setPeakLead(vstr.extractDouble());
        subBus.setOffPkLead(vstr.extractDouble());
        subBus.setCurrentVoltLoadPointID((int)vstr.extractUnsignedInt());
        subBus.setCurrentVoltLoadPointValue(vstr.extractDouble());
        subBus.setVerificationFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
        subBus.setSwitchOverStatus((int)vstr.extractUnsignedInt() == 1 ? true : false);
        subBus.setCurrentwattpointquality(vstr.extractInt());
        subBus.setCurrentvoltpointquality(vstr.extractInt());
        subBus.setTargetvarvalue(vstr.extractDouble());
        subBus.setSolution((String) vstr.restoreObject(SimpleMappings.CString));
        subBus.setOvUvDisabledFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
        subBus.setPeakPFSetPoint(vstr.extractDouble());
        subBus.setOffpeakPFSetPoint(vstr.extractDouble());
        subBus.setControlMethod(ControlMethod.valueOf((String) vstr.restoreObject(SimpleMappings.CString)));
        subBus.setPhaseA(vstr.extractDouble());
        subBus.setPhaseB(vstr.extractDouble());
        subBus.setPhaseC(vstr.extractDouble());
        subBus.setLikeDayControlFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
        subBus.setDisplayOrder(vstr.extractInt());
        subBus.setVoltReductionFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
        subBus.setUsePhaseData((int)vstr.extractUnsignedInt() == 1 ? true : false);
        subBus.setPrimaryBusFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
        subBus.setAlternateBusId((int)vstr.extractUnsignedInt());
        subBus.setDualBusEnabled((int)vstr.extractUnsignedInt() == 1 ? true : false);
        subBus.setStrategyId((int)vstr.extractUnsignedInt());
        
        subBus.setCcFeeders( CollectionExtracter.extractVector(vstr, polystr));
    }
    
    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) {
        throw new UnsupportedOperationException("SaveGuts invalid for " + this.getClass().getName());
    }
    
}