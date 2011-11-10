package com.cannontech.message.capcontrol.defineCollectable;

import java.io.IOException;

import com.cannontech.message.capcontrol.model.CapControlCommand;
import com.cannontech.message.util.DefineCollectableMessage;
import com.roguewave.tools.v2_0.Comparator;
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
        return new CapControlCommand();
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
        return CTICCCOMMAND_ID;
    }
    
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }
    
    public Class<?> getJavaClass() {
        return CapControlCommand.class;
    }
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        CapControlCommand cmd = (CapControlCommand) obj;
        cmd.setMessageId((int)vstr.extractUnsignedInt());
        cmd.setCommandId((int)vstr.extractUnsignedInt());
    }
    
    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException {
        super.saveGuts(obj, vstr, polystr);
        CapControlCommand cmd = (CapControlCommand) obj;
        vstr.insertUnsignedInt(cmd.getMessageId());
        vstr.insertUnsignedInt((long)cmd.getCommandId());
    }
    
}