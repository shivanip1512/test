package com.cannontech.core.dynamic;

import java.util.Set;

import org.joda.time.Instant;

import com.cannontech.core.dao.DuplicateException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.user.YukonUserContext;

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
    LiteState getCurrentStateForNonStatusPoint(LitePoint lp);

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

    /**
     * Sends point data to dispatch.
     * @throws DuplicateException 
     */
    void addPointData(int pointId, double value, Instant timestamp, YukonUserContext context) throws DuplicateException;

    /**
     * Finds and deletes point data by pointId, value and timestamp.
     */
    void deletePointData(int pointId, double value, Instant timestamp, YukonUserContext context);

    /**
     * Updates point data with the new point value.
     * 
     * - Finds and deletes point data by pointId, value and timestamp.
     * - Sends new point data to dispatch. The new point data contains new value and original timestamp. 
     */
    void updatePointData(int pointId, double oldValue, double newValue, Instant timestamp, YukonUserContext context);
    
}
