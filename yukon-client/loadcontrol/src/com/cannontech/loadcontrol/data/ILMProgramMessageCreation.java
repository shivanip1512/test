package com.cannontech.loadcontrol.data;

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
com.cannontech.loadcontrol.messages.LMManualControlRequest createScheduledStartMsg( java.util.Date start, java.util.Date stop, int gearNumber, 
	  	java.util.Date notifyTime, String additionalInfo );
/**
 * Insert the method's description here.
 * Creation date: (7/19/2001 8:50:05 AM)
 */
com.cannontech.loadcontrol.messages.LMManualControlRequest createScheduledStopMsg( java.util.Date start, java.util.Date stop, int gearNumber,
	  	String additionalInfo );
/**
 * Insert the method's description here.
 * Creation date: (7/19/2001 8:50:05 AM)
 */
com.cannontech.loadcontrol.messages.LMManualControlRequest createStartStopNowMsg( java.util.Date stopTime, int gearNumber, String additionalInfo, boolean isStart );
}
