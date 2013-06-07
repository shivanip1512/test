package com.cannontech.message.capcontrol;

import java.io.IOException;

import com.cannontech.messaging.message.capcontrol.VerifyInactiveBanksMessage;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefineCollectableVerifyInactiveBanks extends DefineCollectableVerifyBanks {

    public static final int CTI_VERIFY_BANKS_ID = 532;
    
    public DefineCollectableVerifyInactiveBanks() {
        super();
    }

    public Object create(VirtualInputStream vstr) {
        return new VerifyInactiveBanksMessage();
    }

    public int getCxxClassId() {
        return CTI_VERIFY_BANKS_ID;
    }
    
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }
    
    public Class<VerifyInactiveBanksMessage> getJavaClass() {
        return VerifyInactiveBanksMessage.class;
    }
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {        
        throw new UnsupportedOperationException("RestoreGuts invalid for " + this.getClass().getName());
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException {
        super.saveGuts( obj, vstr, polystr );
    
        VerifyInactiveBanksMessage verifyInactiveBanks = (VerifyInactiveBanksMessage) obj;
        vstr.insertUnsignedInt(verifyInactiveBanks.getCbInactivityTime());
    }
    
}