package com.cannontech.message.capcontrol;

import java.io.IOException;

import com.cannontech.messaging.message.capcontrol.DeleteItemMessage;
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
        return new DeleteItemMessage();
    }

    public int getCxxClassId() {
        return CTI_DELETE_ITEM_MESSAGE_ID;
    }

    public Class<DeleteItemMessage> getJavaClass() {
        return DeleteItemMessage.class;
    }

    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        
        ((DeleteItemMessage) obj).setItemId((int)vstr.extractUnsignedInt());
    }

    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) {
        throw new UnsupportedOperationException("SaveGuts invalid for " + this.getClass().getName());
    }
    
}