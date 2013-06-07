package com.cannontech.message.capcontrol;

import java.io.IOException;

import com.cannontech.messaging.message.capcontrol.ItemCommandMessage;
import com.cannontech.messaging.message.capcontrol.VerifyBanksMessage;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefineCollectableVerifyBanks extends DefineCollectableItemCommand {

    public static final int CTI_VERIFY_BANKS_ID = 514;
    
    public DefineCollectableVerifyBanks() {
        super();
    }

    public Object create(VirtualInputStream vstr) {
        return new VerifyBanksMessage();
    }

    public int getCxxClassId() {
        return CTI_VERIFY_BANKS_ID;
    }
    
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }
    
    public Class<? extends ItemCommandMessage> getJavaClass() {
        return VerifyBanksMessage.class;
    }
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {
        throw new UnsupportedOperationException("RestoreGuts invalid for " + this.getClass().getName());
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException {
        super.saveGuts( obj, vstr, polystr );
    
        VerifyBanksMessage verifySub = (VerifyBanksMessage) obj;
        vstr.insertUnsignedInt(verifySub.isDisableOvUv() ? 1 : 0);
    }
    
}