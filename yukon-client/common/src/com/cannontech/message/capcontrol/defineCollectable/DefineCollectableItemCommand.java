package com.cannontech.message.capcontrol.defineCollectable;

import java.io.IOException;

import com.cannontech.message.capcontrol.model.ItemCommand;
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
        return new ItemCommand();
    }
    
    public int getCxxClassId() {
        return CTICCCOMMAND_ID;
    }
    
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }
    
    public Class<?> getJavaClass() {
        return ItemCommand.class;
    }
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {
        throw new UnsupportedOperationException("RestoreGuts invalid for ItemCommand");
    }
    
    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException{
        super.saveGuts( obj, vstr, polystr );
    
        ItemCommand itemCommand = (ItemCommand) obj;
    
        vstr.insertUnsignedInt((long)itemCommand.getItemId());
    }

}