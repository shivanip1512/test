package com.cannontech.message.capcontrol.defineCollectable;

/**
 * This type was created in VisualAge.
 */
import java.io.IOException;

import com.cannontech.message.capcontrol.model.BankMove;
import com.roguewave.tools.v2_0.Comparator;
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
        return new BankMove();
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
        return CTI_TEMPMOVEBANK_ID;
    }
    
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }
    
    public Class<BankMove> getJavaClass() {
        return BankMove.class;
    }
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) {        
        throw new UnsupportedOperationException("RestoreGuts invalid for " + this.getClass().getName());
    }
    
    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException {
        super.saveGuts( obj, vstr, polystr );
    
        BankMove tmpMove = (BankMove)obj;
    
        vstr.insertUnsignedInt((long) (tmpMove.isPermanentMove() ? 1 : 0));
        vstr.insertUnsignedInt((long) tmpMove.getOldFeederId());
        vstr.insertUnsignedInt((long) tmpMove.getNewFeederId());
        vstr.insertFloat(tmpMove.getDisplayOrder());
        vstr.insertFloat(tmpMove.getCloseOrder());
        vstr.insertFloat(tmpMove.getTripOrder());
    }
    
}