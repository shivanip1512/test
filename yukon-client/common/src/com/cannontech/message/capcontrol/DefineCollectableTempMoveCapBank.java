package com.cannontech.message.capcontrol;

/**
 * This type was created in VisualAge.
 */
import java.io.IOException;

import com.cannontech.messaging.message.capcontrol.BankMoveMessage;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefineCollectableTempMoveCapBank extends DefineCollectableItemCommand {
    
    //RogueWave classId
    public static final int CTI_TEMPMOVEBANK_ID = 513;
    
    public DefineCollectableTempMoveCapBank() {
        super();
    }
    
    public Object create(VirtualInputStream vstr) {
        return new BankMoveMessage();
    }
    
    public int getCxxClassId() {
        return CTI_TEMPMOVEBANK_ID;
    }
    
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }
    
    public Class<BankMoveMessage> getJavaClass() {
        return BankMoveMessage.class;
    }
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) {        
        throw new UnsupportedOperationException("RestoreGuts invalid for " + this.getClass().getName());
    }
    
    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException {
        super.saveGuts( obj, vstr, polystr );
    
        BankMoveMessage tmpMove = (BankMoveMessage)obj;
    
        vstr.insertUnsignedInt((long) (tmpMove.isPermanentMove() ? 1 : 0));
        vstr.insertUnsignedInt((long) tmpMove.getOldFeederId());
        vstr.insertUnsignedInt((long) tmpMove.getNewFeederId());
        vstr.insertFloat(tmpMove.getDisplayOrder());
        vstr.insertFloat(tmpMove.getCloseOrder());
        vstr.insertFloat(tmpMove.getTripOrder());
    }
    
}