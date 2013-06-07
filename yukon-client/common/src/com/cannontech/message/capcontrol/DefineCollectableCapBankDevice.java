package com.cannontech.message.capcontrol;

import java.io.IOException;
import java.util.Date;

import com.cannontech.messaging.message.capcontrol.streamable.CapBankDevice;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableCapBankDevice extends DefineCollectableStreamableCapObject {
    //The roguewave class id
    private static int CTI_CCCAPBANK_ID = 504;
    
    public DefineCollectableCapBankDevice() {
        super();
    }
    
    public Object create(VirtualInputStream vstr) {
        return new CapBankDevice();
    }
    
    public int getCxxClassId() {
        return DefineCollectableCapBankDevice.CTI_CCCAPBANK_ID;
    }
    
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }
    
    public Class<CapBankDevice> getJavaClass() {
        return CapBankDevice.class;
    }
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {
        super.restoreGuts( obj, vstr, polystr );
        
        CapBankDevice capBank = (CapBankDevice) obj;
    
        capBank.setMaxDailyOperation((int)vstr.extractUnsignedInt());
        capBank.setMaxOperationDisableFlag((int)vstr.extractUnsignedInt() == 1 ?true : false);
    
        capBank.setAlarmInhibit((int)vstr.extractUnsignedInt() == 1 ?true : false);
        capBank.setControlInhibit((int)vstr.extractUnsignedInt()== 1 ?true : false);
        capBank.setOperationalState((String) vstr.restoreObject(SimpleMappings.CString));
        capBank.setControllerType((String) vstr.restoreObject(SimpleMappings.CString));
        capBank.setControlDeviceId((int)vstr.extractUnsignedInt());
        capBank.setBankSize((int)vstr.extractUnsignedInt());
        capBank.setTypeOfSwitch((String) vstr.restoreObject(SimpleMappings.CString));
        capBank.setSwitchManufacture((String) vstr.restoreObject(SimpleMappings.CString));    
        capBank.setMapLocationId((String) vstr.restoreObject(SimpleMappings.CString));
        capBank.setRecloseDelay((int)vstr.extractUnsignedInt());
        
        capBank.setControlOrder(vstr.extractFloat());
        
        capBank.setStatusPointId((int)vstr.extractUnsignedInt());
        capBank.setControlStatus((int)vstr.extractUnsignedInt());
        capBank.setOperationAnalogPointId((int)vstr.extractUnsignedInt());
        capBank.setTotalOperations((int)vstr.extractUnsignedInt());    
        capBank.setLastStatusChangeTime((Date)vstr.restoreObject(SimpleMappings.Time));    
        capBank.setTagControlStatus((int)vstr.extractUnsignedInt());
    
        capBank.setOrigFeederId((int)vstr.extractUnsignedInt());
        capBank.setCurrentDailyOperations((int)vstr.extractUnsignedInt());    
        capBank.setIgnoreFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
        capBank.setIgnoreReason((int) vstr.extractUnsignedInt());
        capBank.setOvUVDisabled((int)vstr.extractUnsignedInt() == 1 ? true : false);
        capBank.setTripOrder(vstr.extractFloat());
        capBank.setCloseOrder(vstr.extractFloat());
        capBank.setControlDeviceType((String) vstr.restoreObject(SimpleMappings.CString));
        capBank.setBeforeVars((String) vstr.restoreObject(SimpleMappings.CString));
        capBank.setAfterVars((String) vstr.restoreObject(SimpleMappings.CString));
        capBank.setPercentChange((String) vstr.restoreObject(SimpleMappings.CString));
        capBank.setMaxDailyOperationHitFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
        capBank.setOvuvSituationFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
        capBank.setControlStatusQuality((int)vstr.extractUnsignedInt());
        capBank.setLocalControlFlag((int)vstr.extractUnsignedInt() == 1 ? true : false);
        capBank.setPartialPhaseInfo((String) vstr.restoreObject(SimpleMappings.CString));
    }
    
    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException {
        super.saveGuts( obj, vstr, polystr );
    
        CapBankDevice capBank = (CapBankDevice) obj;
    
        vstr.insertUnsignedInt(capBank.getMaxDailyOperation());
        vstr.insertUnsignedInt(capBank.getMaxOperationDisableFlag() ? 1 : 0);
    
        vstr.insertUnsignedInt(capBank.isAlarmInhibit() ? 1 : 0);
        vstr.insertUnsignedInt(capBank.isControlInhibit() ? 1 : 0);
        vstr.saveObject(capBank.getOperationalState(), SimpleMappings.CString);
        vstr.saveObject(capBank.getControllerType(), SimpleMappings.CString);
        vstr.insertUnsignedInt(capBank.getControlDeviceId());
    
        vstr.insertUnsignedInt(capBank.getBankSize());
        vstr.saveObject(capBank.getTypeOfSwitch(), SimpleMappings.CString);
        vstr.saveObject(capBank.getSwitchManufacture(), SimpleMappings.CString);
        vstr.saveObject(capBank.getMapLocationId(), SimpleMappings.CString);
        vstr.insertUnsignedInt(capBank.getRecloseDelay());
        
        vstr.insertFloat(capBank.getControlOrder());
        vstr.insertUnsignedInt(capBank.getStatusPointId());
        vstr.insertUnsignedInt(capBank.getControlStatus());
        vstr.insertUnsignedInt(capBank.getOperationAnalogPointId());
        vstr.insertUnsignedInt(capBank.getTotalOperations());
        vstr.saveObject(capBank.getLastStatusChangeTime(), SimpleMappings.Time);
        vstr.insertUnsignedInt(capBank.getTagControlStatus());
    
        vstr.insertUnsignedInt(capBank.getOrigFeederId());
        vstr.insertUnsignedInt(capBank.getCurrentDailyOperations());
        vstr.insertUnsignedInt(capBank.isIgnoreFlag() ? 1 : 0);
        vstr.insertUnsignedInt(capBank.getIgnoreReason());
        vstr.insertUnsignedInt(capBank.getOvUVDisabled() ? 1 : 0);
        vstr.insertFloat(capBank.getTripOrder()); 
        vstr.insertFloat(capBank.getCloseOrder());
        vstr.saveObject(capBank.getControlDeviceType(), SimpleMappings.CString);
        vstr.saveObject(capBank.getBeforeVars(), SimpleMappings.CString);
        vstr.saveObject(capBank.getAfterVars(), SimpleMappings.CString);
        vstr.saveObject(capBank.getPercentChange(), SimpleMappings.CString);
        vstr.insertUnsignedInt(capBank.getMaxDailyOperationHitFlag() ? 1 : 0);
        vstr.insertUnsignedInt(capBank.getOvuvSituationFlag() ? 1 : 0);
        vstr.insertUnsignedInt(capBank.getControlStatusQuality());
        vstr.insertUnsignedInt(capBank.getLocalControlFlag() ? 1 : 0);
        vstr.saveObject(capBank.getPartialPhaseInfo(), SimpleMappings.CString);
    }
    
}