package com.cannontech.message.capcontrol;

import java.io.IOException;

import com.cannontech.message.util.DefineCollectableMessage;
import com.cannontech.messaging.message.capcontrol.CommandMessage;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefineCollectableCapControlCommand extends DefineCollectableMessage {

    //RogueWave classId
    public static final int CTICCCOMMAND_ID = 533;
    
    public DefineCollectableCapControlCommand() {
        super();
    }
    
    public Object create(VirtualInputStream vstr) {
        return new CommandMessage();
    }
    
    public int getCxxClassId() {
        return CTICCCOMMAND_ID;
    }
    
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }
    
    public Class<?> getJavaClass() {
        return CommandMessage.class;
    }
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        CommandMessage cmd = (CommandMessage) obj;
        cmd.setMessageId((int)vstr.extractUnsignedInt());
        cmd.setCommandId((int)vstr.extractUnsignedInt());
    }
    
    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException {
        super.saveGuts(obj, vstr, polystr);
        CommandMessage cmd = (CommandMessage) obj;
        vstr.insertUnsignedInt(cmd.getMessageId());
        vstr.insertUnsignedInt((long)cmd.getCommandId());
    }
    
}