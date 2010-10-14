package com.cannontech.yukon.cbc;

import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;

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
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws java.io.IOException 
    {
        super.restoreGuts(obj, vstr, polystr);
        
        VoltageRegulatorFlags regulator = (VoltageRegulatorFlags)obj;

        regulator.setLowerTap(((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false));
        regulator.setRaiseTap(((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false));
        regulator.setAutoRemote(((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false));
        regulator.setAutoRemoteManual(((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false));
    }
}
