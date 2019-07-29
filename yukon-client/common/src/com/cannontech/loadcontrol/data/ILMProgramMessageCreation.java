package com.cannontech.loadcontrol.data;

import com.cannontech.dr.model.ProgramOriginSource;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;

/**
 * Insert the type's description here.
 * Creation date: (7/19/2001 8:49:17 AM)
 * @author: 
 */
public interface ILMProgramMessageCreation {
/**
 * Insert the method's description here.
 * Creation date: (7/19/2001 8:50:05 AM)
 */
LMManualControlRequest createScheduledStartMsg( java.util.Date start, java.util.Date stop, int gearNumber, 
        java.util.Date notifyTime, String additionalInfo, int constraintFlag, ProgramOriginSource programOriginSource);
/**
 * Insert the method's description here.
 * Creation date: (7/19/2001 8:50:05 AM)
 */
LMManualControlRequest createScheduledStopMsg( java.util.Date start, java.util.Date stop, int gearNumber,
        String additionalInfo, ProgramOriginSource programOriginSource);
/**
 * Insert the method's description here.
 * Creation date: (7/19/2001 8:50:05 AM)
 */
LMManualControlRequest createStartStopNowMsg( java.util.Date stopTime, int gearNumber, String additionalInfo,
        boolean isStart, int constraintFlag, ProgramOriginSource programOriginSource);
}
