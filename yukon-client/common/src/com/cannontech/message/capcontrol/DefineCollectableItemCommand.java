package com.cannontech.message.capcontrol;

import java.io.IOException;

import com.cannontech.messaging.message.capcontrol.ItemCommandMessage;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefineCollectableItemCommand extends DefineCollectableCapControlCommand {

    //RogueWave classId
    public static final int CTICCCOMMAND_ID = 531;
    
    public DefineCollectableItemCommand() {
        super();
    }
    
    public Object create(VirtualInputStream vstr) {
        return new ItemCommandMessage();
    }
    
    public int getCxxClassId() {
        return CTICCCOMMAND_ID;
    }
    
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }
    
    public Class<?> getJavaClass() {
        return ItemCommandMessage.class;
    }
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {
        throw new UnsupportedOperationException("RestoreGuts invalid for ItemCommand");
    }
    
    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException{
        super.saveGuts( obj, vstr, polystr );
    
        ItemCommandMessage itemCommand = (ItemCommandMessage) obj;
    
        vstr.insertUnsignedInt((long)itemCommand.getItemId());
    }

}