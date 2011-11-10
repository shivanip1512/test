package com.cannontech.message.capcontrol.defineCollectable;

import java.io.IOException;

import com.cannontech.message.capcontrol.model.VerifyBanks;
import com.roguewave.tools.v2_0.Comparator;
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
        return new VerifyBanks();
    }

    public Comparator getComparator() {
        return new Comparator() {
          public int compare(Object x, Object y) {
                if( x == y )
                    return 0;
                else
                    return -1;
          }
        };
    }
    
    public int getCxxClassId() {
        return CTI_VERIFY_BANKS_ID;
    }
    
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }
    
    public Class<VerifyBanks> getJavaClass() {
        return VerifyBanks.class;
    }
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {
        throw new UnsupportedOperationException("RestoreGuts invalid for " + this.getClass().getName());
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException {
        super.saveGuts( obj, vstr, polystr );
    
        VerifyBanks verifySub = (VerifyBanks) obj;
        vstr.insertUnsignedInt(verifySub.isDisableOvUv() ? 1 : 0);
    }
    
}