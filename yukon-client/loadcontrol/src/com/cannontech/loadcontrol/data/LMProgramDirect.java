package com.cannontech.loadcontrol.data;

/**
 * Insert the type's description here.
 * Creation date: (8/17/00 3:06:09 PM)
 * @author: 
 */
public class LMProgramDirect extends LMProgramBase implements LMProgramMessageCreation
{
	private Integer currentGearNumber = null;
	private Integer lastGroupControlled = null;
	private java.util.GregorianCalendar directStartTime = null;
	private java.util.GregorianCalendar directStopTime = null;

	private java.util.Vector directGearVector = null;


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
public com.cannontech.loadcontrol.messages.LMManualControlMsg createScheduledStartMsg( java.util.Date start, java.util.Date stop, int gearNumber, java.util.Date notifyTime, String additionalInfo )
{
	com.cannontech.loadcontrol.messages.LMManualControlMsg msg = new com.cannontech.loadcontrol.messages.LMManualControlMsg();
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
	
	msg.setCommand( com.cannontech.loadcontrol.messages.LMManualControlMsg.SCHEDULED_START );		
		

	return msg;
}
/**
 * Insert the method's description here.
 * Creation date: (7/19/2001 8:50:05 AM)
 */
public com.cannontech.loadcontrol.messages.LMManualControlMsg createScheduledStopMsg( java.util.Date start, java.util.Date stop, int gearNumber, String additionalInfo )
{
	com.cannontech.loadcontrol.messages.LMManualControlMsg msg = new com.cannontech.loadcontrol.messages.LMManualControlMsg();
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
	
	msg.setCommand( com.cannontech.loadcontrol.messages.LMManualControlMsg.SCHEDULED_STOP );
		

	return msg;
}
/**
 * Insert the method's description here.
 * Creation date: (7/19/2001 8:50:05 AM)
 */
public com.cannontech.loadcontrol.messages.LMManualControlMsg createStartStopNowMsg( java.util.Date stopTime, int gearNumber, String additionalInfo, boolean isStart )
{
	com.cannontech.loadcontrol.messages.LMManualControlMsg msg = new com.cannontech.loadcontrol.messages.LMManualControlMsg();	
	java.util.GregorianCalendar cStop = new java.util.GregorianCalendar();
	cStop.setTime(stopTime);

	
	msg.setStartGear( gearNumber );

	if( stopTime != null )
		msg.setStopTime(cStop);

	if( additionalInfo != null )
		msg.setAddditionalInfo(additionalInfo)	;

	msg.setYukonID( getYukonID().intValue() );

	if( isStart )
		msg.setCommand( com.cannontech.loadcontrol.messages.LMManualControlMsg.START_NOW );
	else
		msg.setCommand( com.cannontech.loadcontrol.messages.LMManualControlMsg.STOP_NOW );

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
 */
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
public void setCurrentGearNumber(java.lang.Integer newCurrentGearNumber) {
	currentGearNumber = newCurrentGearNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:30:28 PM)
 * @param newDirectGearVector java.util.Vector
 */
public void setDirectGearVector(java.util.Vector newDirectGearVector) {
	directGearVector = newDirectGearVector;
}
/**
 * Insert the method's description here.
 * Creation date: (4/25/2001 10:57:20 AM)
 * @param newDirectStartTime java.util.GregorianCalendar
 */
public void setDirectStartTime(java.util.GregorianCalendar newDirectStartTime) {
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
public void setLastGroupControlled(java.lang.Integer newLastGroupControlled) {
	lastGroupControlled = newLastGroupControlled;
}
}
