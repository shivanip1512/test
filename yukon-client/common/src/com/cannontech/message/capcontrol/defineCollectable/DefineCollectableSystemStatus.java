package com.cannontech.message.capcontrol.defineCollectable;

import java.io.IOException;

import com.cannontech.message.capcontrol.model.SystemStatus;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefineCollectableSystemStatus extends DefineCollectableCapControlMessage {

    //Id for RogueWave. Must match C++
    public static final int CTI_SYSTEM_STATUS_MESSAGE_ID = 534;

    public DefineCollectableSystemStatus() {
        super();
    }

    public Object create(VirtualInputStream vstr) {
        return new SystemStatus();
    }

    public int getCxxClassId() {
        return CTI_SYSTEM_STATUS_MESSAGE_ID;
    }

    public Class<SystemStatus> getJavaClass() {
        return SystemStatus.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        
        ((SystemStatus) obj).setEnabled((int)vstr.extractUnsignedInt() == 1 ? true : false);
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) {
        throw new UnsupportedOperationException("SaveGuts invalid for " + this.getClass().getName());
    }
    
}