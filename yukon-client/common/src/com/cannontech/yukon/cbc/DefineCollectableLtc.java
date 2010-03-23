package com.cannontech.yukon.cbc;

import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;

public class DefineCollectableLtc extends DefineCollectableStreamableCapObject {

    private static int CTI_LTC_ID = 527;
    
    public DefineCollectableLtc() {
        
    }
    
    public Object create(VirtualInputStream vstr) throws java.io.IOException {
        return new Ltc();
    }
    
    @Override
    public int getCxxClassId() {
        return CTI_LTC_ID;
    }
    
    @Override
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }
    
    @Override
    public Class<Ltc> getJavaClass() {
        return Ltc.class;
    }
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws java.io.IOException 
    {
        super.restoreGuts(obj, vstr, polystr);
        
        Ltc ltc = (Ltc)obj;

        ltc.setLowerTap(((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false));
        ltc.setRaiseTap(((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false));
        ltc.setAutoRemote(((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false));
        ltc.setAutoRemoteManual(((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false));
    }
}
