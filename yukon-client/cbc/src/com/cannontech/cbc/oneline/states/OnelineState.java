package com.cannontech.cbc.oneline.states;

import com.cannontech.yukon.cbc.StreamableCapObject;

public interface OnelineState {

    int getLiteStateId(StreamableCapObject o, int state);
    
    void setGroupName(String n);
    String getGroupName();
}
