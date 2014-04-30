package com.cannontech.core.dynamic;

import java.util.Set;

import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.message.dispatch.message.Signal;

public interface PointService {

    /**
     * Uses alarm signals to derive the state of a non status point.
     * If no signals exist or if none are active the first state of the 
     * state group is return.  The first state is considered the 'normal'
     * state; see DefaultAnalog, DefaultCalculated, and DefaultAccumulator
     * state groups.
     * 
     * @return LiteState The current state of this point.
     */
    LiteState getCurrentStateForNonStatusPoint(int pointId);

    /**
     * Uses alarm signals to derive the state of a non status point.
     * If no signals exist or if none are active the first state of the 
     * state group is return.  The first state is considered the 'normal'
     * state; see DefaultAnalog, DefaultCalculated, and DefaultAccumulator
     * state groups.
     * 
     * @return LiteState The current state of this point.
     */
    LiteState getCurrentStateForNonStatusPoint(LitePoint lp, Set<Signal> signals);
    
}
