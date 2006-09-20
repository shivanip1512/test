package com.cannontech.loadcontrol.data;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.cannontech.loadcontrol.messages.LMManualControlRequest;

/**
 * Creation date: (8/17/00 3:06:09 PM)
 * @author: 
 */
public class LMProgramDirect extends LMProgramBase implements IGearProgram
{
	private Integer currentGearNumber = null;
	private Integer lastGroupControlled = null;
	private GregorianCalendar directStartTime = null;
	private GregorianCalendar directStopTime = null;
	private GregorianCalendar notifyActiveTime = null;
	private GregorianCalendar notifyInactiveTime = null;	
	private GregorianCalendar startedRampingOut = null;
	private Integer triggerOffset = null;
	private Integer triggerRestoreOffset = null;	
	private boolean constraintOverride = false;	
    private String addtionalInfo = null;
	
	/**
	 * @return Returns the triggerOffset.
	 */
	
    public Integer getTriggerOffset() {
		return triggerOffset;
	}
	/**
	 * @param triggerOffset The triggerOffset to set.
	 */
	public void setTriggerOffset(Integer triggerOffset) {
		this.triggerOffset = triggerOffset;
	}
	/**
	 * @return Returns the triggerRestoreOffset.
	 */
	public Integer getTriggerRestoreOffset() {
		return triggerRestoreOffset;
	}
	/**
	 * @param triggerRestoreOffset The triggerRestoreOffset to set.
	 */
	public void setTriggerRestoreOffset(Integer triggerRestoreOffset) {
		this.triggerRestoreOffset = triggerRestoreOffset;
	}
	private Vector directGearVector = null;
	private Vector activeMasterProgramsVector = null;
	private Vector activeSubordinateProgramsVector = null;

/**
 * Insert the method's description here.
 * Creation date: (4/9/2001 10:25:26 AM)
 */
public LMProgramDirect()
{
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (7/19/2001 8:50:05 AM)
 */
public LMManualControlRequest createScheduledStartMsg( Date start, Date stop,
		int gearNumber, Date notifyTime, String additionalInfo,
		int constraintFlag )
{
	LMManualControlRequest msg = new LMManualControlRequest();
	java.util.GregorianCalendar cStart = new java.util.GregorianCalendar();
	java.util.GregorianCalendar cStop = new java.util.GregorianCalendar();
	cStart.setTime( start );
	cStop.setTime( stop );
	
	msg.setStartTime( cStart );
	msg.setStopTime( cStop );
	msg.setStartGear( gearNumber );

	if( additionalInfo != null )
		msg.setAddditionalInfo(additionalInfo)	;

	msg.setYukonID( getYukonID().intValue() );
	msg.setStartPriority( getStartPriority().intValue() );
	
	msg.setCommand( LMManualControlRequest.SCHEDULED_START );
	msg.setConstraintFlag( constraintFlag );		
		

	return msg;
}
/**
 * Insert the method's description here.
 * Creation date: (7/19/2001 8:50:05 AM)
 */
public LMManualControlRequest createScheduledStopMsg( Date start, Date stop, int gearNumber, String additionalInfo )
{
	LMManualControlRequest msg = new LMManualControlRequest();
	java.util.GregorianCalendar cStart = new java.util.GregorianCalendar();
	java.util.GregorianCalendar cStop = new java.util.GregorianCalendar();
	cStart.setTime( start );
	cStop.setTime( stop );
	
	msg.setStartTime( cStart );
	msg.setStopTime( cStop );
	msg.setStartGear( gearNumber );

	if( additionalInfo != null )
		msg.setAddditionalInfo(additionalInfo)	;

	
	msg.setYukonID( getYukonID().intValue() );
	msg.setStartPriority( getStartPriority().intValue() );
	
	msg.setCommand( LMManualControlRequest.SCHEDULED_STOP );
		

	return msg;
}
/**
 * Insert the method's description here.
 * Creation date: (7/19/2001 8:50:05 AM)
 */
public LMManualControlRequest createStartStopNowMsg( Date stopTime, int gearNumber,
		String additionalInfo, boolean isStart,
		int constraintFlag )
{
	LMManualControlRequest msg = new LMManualControlRequest();	
	java.util.GregorianCalendar cStop = new java.util.GregorianCalendar();
	cStop.setTime(stopTime);

	
	msg.setStartGear( gearNumber );

	if( stopTime != null )
		msg.setStopTime(cStop);

	if( additionalInfo != null )
		msg.setAddditionalInfo(additionalInfo)	;

	msg.setYukonID( getYukonID().intValue() );
	msg.setStartPriority( getStartPriority().intValue() );

	if( isStart ) {
		msg.setCommand( LMManualControlRequest.START_NOW );
		msg.setConstraintFlag( constraintFlag );
	}
	else
		msg.setCommand( LMManualControlRequest.STOP_NOW );

	return msg;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:30:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentGearNumber() {
	return currentGearNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:30:28 PM)
 * @return java.util.Vector
 */
public java.util.Vector getDirectGearVector() {
	return directGearVector;
}
/**
 * Insert the method's description here.
 * Creation date: (4/25/2001 10:57:20 AM)
 * @return java.util.GregorianCalendar
// */
public java.util.GregorianCalendar getDirectStartTime() {
	return directStartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/25/2001 10:57:20 AM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getDirectStopTime() {
	return directStopTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:30:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getLastGroupControlled() {
	return lastGroupControlled;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @return java.lang.String
 */
public java.util.GregorianCalendar getStartTime()
{
	return getDirectStartTime();
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @return java.lang.String
 */
public java.util.GregorianCalendar getStopTime()
{
	return getDirectStopTime();
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:30:28 PM)
 * @param newCurrentGearNumber java.lang.Integer
 */
public void setCurrentGearNumber(Integer newCurrentGearNumber) {
	currentGearNumber = newCurrentGearNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:30:28 PM)
 * @param newDirectGearVector java.util.Vector
 */
public void setDirectGearVector(Vector newDirectGearVector) {
	directGearVector = newDirectGearVector;
}
/**
 * Insert the method's description here.
 * Creation date: (4/25/2001 10:57:20 AM)
 * @param newDirectStartTime java.util.GregorianCalendar
 */
public void setDirectStartTime(GregorianCalendar newDirectStartTime) {
	directStartTime = newDirectStartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/25/2001 10:57:20 AM)
 * @param newDirectStopTime java.util.GregorianCalendar
 */
public void setDirectStopTime(java.util.GregorianCalendar newDirectStopTime) {
	directStopTime = newDirectStopTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:30:28 PM)
 * @param newLastGroupControlled java.lang.Integer
 */
public void setLastGroupControlled(Integer newLastGroupControlled) {
	lastGroupControlled = newLastGroupControlled;
}
	/**
	 * @return
	 */
	public java.util.GregorianCalendar getStartedRampingOut() {
		return startedRampingOut;
	}

	/**
	 * @param calendar
	 */
	public void setStartedRampingOut(GregorianCalendar calendar) {
		startedRampingOut = calendar;
	}

	/**
	 * @return Returns the activeMasterProgramsVector.
	 */
	public Vector getActiveMasterPrograms() {
		return activeMasterProgramsVector;
	}
	/**
	 * @param activeMasterProgramsVector The activeMasterProgramsVector to set.
	 */
	public void setActiveMasterPrograms(Vector activeMasterProgramsVector) {
		this.activeMasterProgramsVector = activeMasterProgramsVector;
	}
	/**
	 * @return Returns the activeSubordinateProgramsVector.
	 */
	public Vector getActiveSubordinatePrograms() {
		return activeSubordinateProgramsVector;
	}
	/**
	 * @param activeSubordinateProgramsVector The activeSubordinateProgramsVector to set.
	 */
	public void setActiveSubordinatePrograms(
			Vector activeSubordinateProgramsVector) {
		this.activeSubordinateProgramsVector = activeSubordinateProgramsVector;
	}
	public GregorianCalendar getNotifyActiveTime() {
		return notifyActiveTime;
	}
	public void setNotifyActiveTime(GregorianCalendar notifyActiveTime) {
		this.notifyActiveTime = notifyActiveTime;
	}
	public GregorianCalendar getNotifyInactiveTime() {
		return notifyInactiveTime;
	}
	public void setNotifyInactiveTime(GregorianCalendar notifyInactiveTime) {
		this.notifyInactiveTime = notifyInactiveTime;
	}

	/**
	 * @return
	 */
	public boolean isConstraintOverride() {
		return constraintOverride;
	}

	/**
	 * @param b
	 */
	public void setConstraintOverride(boolean b) {
		constraintOverride = b;
	}
    public String getAddtionalInfo() {
        return addtionalInfo;
    }
    public void setAddtionalInfo(String addtionalInfo) {
        this.addtionalInfo = addtionalInfo;
    }

}
