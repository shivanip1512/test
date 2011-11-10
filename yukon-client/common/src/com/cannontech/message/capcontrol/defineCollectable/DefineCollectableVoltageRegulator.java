package com.cannontech.message.capcontrol.defineCollectable;

import java.io.IOException;
import java.util.Date;

import com.cannontech.capcontrol.TapOperation;
import com.cannontech.message.capcontrol.streamable.VoltageRegulatorFlags;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableVoltageRegulator extends DefineCollectableStreamableCapObject {

    private static int CTI_VOLTAGE_REGULATOR_ID = 527;
    
    public DefineCollectableVoltageRegulator() {
        
    }
    
    public Object create(VirtualInputStream vstr) throws java.io.IOException {
        return new VoltageRegulatorFlags();
    }
    
    @Override
    public int getCxxClassId() {
        return CTI_VOLTAGE_REGULATOR_ID;
    }
    
    @Override
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }
    
    @Override
    public Class<VoltageRegulatorFlags> getJavaClass() {
        return VoltageRegulatorFlags.class;
    }
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        
        VoltageRegulatorFlags regulator = (VoltageRegulatorFlags)obj;

        int tapOperationId = (int)vstr.extractUnsignedInt();
        TapOperation operation = TapOperation.getForTapOperationId(tapOperationId);
        regulator.setLastOperation(operation);
        
        Date timeStamp = (Date) vstr.restoreObject(SimpleMappings.Time);
        regulator.setLastOperationTime(timeStamp);

        regulator.setRecentOperation((int)vstr.extractUnsignedInt() == 1 ? true : false);
        regulator.setAutoRemote((int)vstr.extractUnsignedInt() == 1 ? true : false);
        regulator.setAutoRemoteManual((int)vstr.extractUnsignedInt() == 1 ? true : false);
    }
    
}