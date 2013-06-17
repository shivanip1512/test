package com.cannontech.cbc.oneline.states;

import com.cannontech.message.capcontrol.streamable.StreamableCapObject;

public interface OnelineState {

    int getLiteStateId(StreamableCapObject o, int state);
    
    void setGroupName(String n);
    String getGroupName();
}
