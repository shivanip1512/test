package com.cannontech.message.capcontrol.defineCollectable;

import java.io.IOException;

import com.cannontech.message.capcontrol.model.ChangeOpState;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableChangeOpState extends DefineCollectableItemCommand {

    //RogueWave classId
    public static final int CTICCCOMMAND_ID = 530;
    
    public DefineCollectableChangeOpState() {
        super();
    }
    
    public Object create(VirtualInputStream vstr) {
        return new ChangeOpState();
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
    
    public Class<ChangeOpState> getJavaClass() {
        return ChangeOpState.class;
    }
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) {
        throw new UnsupportedOperationException("RestoreGuts invalid for " + this.getClass().getName());
    }
    
    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException{
        super.saveGuts(obj, vstr, polystr);
        ChangeOpState cmd = (ChangeOpState) obj;
        vstr.saveObject(cmd.getState().getDatabaseRepresentation(), SimpleMappings.CString);
    }

}