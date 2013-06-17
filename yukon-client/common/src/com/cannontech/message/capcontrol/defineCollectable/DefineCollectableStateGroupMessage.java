package com.cannontech.message.capcontrol.defineCollectable;

import java.io.IOException;
import java.util.Vector;

import com.cannontech.database.db.state.State;
import com.cannontech.message.capcontrol.model.States;
import com.cannontech.message.util.CollectionExtracter;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

public class DefineCollectableStateGroupMessage extends DefineCollectableCapControlMessage {
    
    //RogueWave classId
    public static final int CTISTATEGROUP_MESSAGE_ID = 508;
    
    public DefineCollectableStateGroupMessage() {
        super();
    }
    
    public Object create(VirtualInputStream vstr) {
        return new States();
    }
    
    public Comparator getComparator() {
        return new Comparator() {
          public int compare(Object x, Object y) {
                if( x == y ) {
                    return 0;
                } else {
                    return -1;
                }
            }
        };
    }
    
    public int getCxxClassId() {
        return DefineCollectableStateGroupMessage.CTISTATEGROUP_MESSAGE_ID;
    }
    
    public Class<States> getJavaClass() {
        return States.class;
    }
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {
        super.restoreGuts(obj, vstr, polystr);
        Vector<State> stateStore = CollectionExtracter.extractVector(vstr,polystr);
        ((States) obj).setStates(stateStore);
    }
    
    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) {
        throw new UnsupportedOperationException("SaveGuts invalid for " + this.getClass().getName());
    }
    
}