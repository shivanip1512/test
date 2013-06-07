package com.cannontech.message.capcontrol;

import java.io.IOException;

import com.cannontech.messaging.message.capcontrol.VerifySelectedBankMessage;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;


public class DefineCollectableVerifySelectedBank extends DefineCollectableVerifyBanks {
    public static final int CTI_VERIFY_SELECTED_BANK_ID = 536;
    
    public DefineCollectableVerifySelectedBank() {
        super();
    }

    public Object create(VirtualInputStream vstr) {
        return new VerifySelectedBankMessage();
    }

    public int getCxxClassId() {
        return CTI_VERIFY_SELECTED_BANK_ID;
    }
    
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }
    
    public Class<VerifySelectedBankMessage> getJavaClass() {
        return VerifySelectedBankMessage.class;
    }
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {        
        throw new UnsupportedOperationException("RestoreGuts invalid for " + this.getClass().getName());
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException {
        super.saveGuts( obj, vstr, polystr );
    
        VerifySelectedBankMessage verifySelectedBank = (VerifySelectedBankMessage) obj;
        vstr.insertUnsignedInt(verifySelectedBank.getBankId());
    }
}
