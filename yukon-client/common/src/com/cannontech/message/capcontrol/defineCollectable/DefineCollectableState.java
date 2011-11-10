package com.cannontech.message.capcontrol.defineCollectable;

/**
 * This type was created in VisualAge.
 */
import java.io.IOException;

import com.cannontech.database.db.state.State;
import com.cannontech.database.db.state.StateGroupUtils;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableState implements DefineCollectable {
    //The roguewave class id
    private static int CTICCSTATE_ID = 507;
    
    public DefineCollectableState() {
        super();
    }

    public Object create(VirtualInputStream vstr) {
        return new State();
    }
    
    public Comparator getComparator() {
        return new Comparator() {
            public int compare(Object x, Object y) {
                return (int) (((State)x).getRawState() - ((State)y).getRawState());
            }
        };
        
    }
    
    public int getCxxClassId() {
        return CTICCSTATE_ID;
    }
    
    public String getCxxStringId() {
        return DefineCollectable.NO_STRINGID;
    }
    
    public Class<State> getJavaClass() {
        return State.class;
    }
    
    public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws IOException {
        State state = (State) obj;
        
        String text = (String) vstr.restoreObject(SimpleMappings.CString);
        Integer fgColor = (int)vstr.extractUnsignedInt();
        Integer bgColor = (int)vstr.extractUnsignedInt();
    
        state.setText(text);
        state.setForegroundColor(fgColor);
        state.setBackgroundColor(bgColor);
    
        // set the stateGroupId to that of all CapBank states
        state.setStateGroupID(StateGroupUtils.STATEGROUPID_CAPBANK);
    }
    
    public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws IOException {
        State state = (State) obj;
        
        vstr.saveObject(state.getText(), SimpleMappings.CString);
        vstr.insertUnsignedInt(state.getForegroundColor());
        vstr.insertUnsignedInt(state.getBackgroundColor());
    }
    
}