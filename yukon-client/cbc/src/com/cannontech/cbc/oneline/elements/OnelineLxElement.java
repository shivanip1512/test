package com.cannontech.cbc.oneline.elements;

import com.cannontech.cbc.oneline.states.OnelineState;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;

public interface OnelineLxElement {

    public abstract boolean update(boolean change);

    public abstract StreamableCapObject getStreamable(String compName);

    public abstract OnelineState getDynamicState();

}