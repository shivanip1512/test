package com.cannontech.message.capcontrol.defineCollectable;

import java.io.IOException;

import com.cannontech.message.capcontrol.model.DeleteItem;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefineCollectableDeleteItem extends DefineCollectableCapControlMessage {

    //Id for RogueWave. Must match C++
    public static final int CTI_DELETE_ITEM_MESSAGE_ID = 535;

    public DefineCollectableDeleteItem() {
        super();
    }

    public Object create(VirtualInputStream vstr) {
        return new DeleteItem();
    }

    public int getCxxClassId() {
        return CTI_DELETE_ITEM_MESSAGE_ID;
    }

    public Class<DeleteItem> getJavaClass() {
        return DeleteItem.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        
        ((DeleteItem) obj).setItemId((int)vstr.extractUnsignedInt());
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) {
        throw new UnsupportedOperationException("SaveGuts invalid for " + this.getClass().getName());
    }
    
}