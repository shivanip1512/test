package com.cannontech.loadcontrol.data;

/**
 * Insert the type's description here.
 * Creation date: (8/17/00 3:06:09 PM)
 * @author: 
 */
import com.cannontech.loadcontrol.messages.LMManualControlRequest;

public class LMProgramCurtailment extends LMProgramBase implements ILMProgramMessageCreation
{
	public static String STATUS_NULL = "Null";
	public static String STATUS_SCHEDULED = "Scheduled";
	public static String STATUS_NOTIFIED = "Notified";
	public static String STATUS_CANCELED = "Canceled";
	public static String STATUS_ACTIVE = "Active";
	public static String STATUS_STOP_EARLY = "StopEarly";
	public static String STATUS_COMPLETED = "Completed";

	private Integer minNotifyTime = null;
	private String heading = null;
	private String messageHeader = null;
	private String messageFooter = null;
	private Integer ackTimeLimit = null;
	private String canceledMsg = null;
	private String stoppedEarlyMsg = null;
	private Integer curtailReferenceId = null;
	private java.util.GregorianCalendar actionDateTime = null;
	private java.util.GregorianCalendar notificationDateTime = null;
	private java.util.GregorianCalendar curtailmentStartTime = null;
	private java.util.GregorianCalendar curtailmentStopTime = null;
	private String runStatus = null;
	private String additionalInfo = null;

/**
 * Insert the method's description here.
 * Creation date: (4/9/2001 10:24:06 AM)
 */
public LMProgramCurtailment() 
{
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (7/19/2001 8:50:05 AM)
 */
public LMManualControlRequest createScheduledStartMsg( java.util.Date start, 
					java.util.Date stop, int gearNumber, java.util.Date notifyTime, 
					String additionalInfo )
{
	LMManualControlRequest msg = new LMManualControlRequest();
	java.util.GregorianCalendar cStart = new java.util.GregorianCalendar();
	cStart.setTime(start);
	
	java.util.GregorianCalendar cStop = new java.util.GregorianCalendar();
	cStop.setTime(stop);
	
	java.util.GregorianCalendar cNotif = new java.util.GregorianCalendar();
	cNotif.setTime(
		(notifyTime == null ? new java.util.Date() : notifyTime) );

	msg.setStartTime(cStart);
	msg.setStopTime(cStop);
	msg.setNotifyTime(cNotif);

	if( additionalInfo != null )
		msg.setAddditionalInfo(additionalInfo)	;
	
	msg.setCommand( com.cannontech.loadcontrol.messages.LMManualControlRequest.SCHEDULED_START );
	msg.setStartPriority( getDefaultPriority().intValue() );
	
	msg.setYukonID( getYukonID().intValue() );

	return msg;
}
/**
 * Insert the method's description here.
 * Creation date: (7/19/2001 8:50:05 AM)
 */
public com.cannontech.loadcontrol.messages.LMManualControlRequest createScheduledStopMsg( java.util.Date start, java.util.Date stop, int gearNumber, String additionalInfo )
{
	com.cannontech.loadcontrol.messages.LMManualControlRequest msg = new com.cannontech.loadcontrol.messages.LMManualControlRequest();
	java.util.GregorianCalendar cStart = new java.util.GregorianCalendar();
	cStart.setTime(start);
	java.util.GregorianCalendar cStop = new java.util.GregorianCalendar();
	cStop.setTime(stop);


	msg.setStartTime(cStart);
	msg.setStopTime(cStop);

	if( additionalInfo != null )
		msg.setAddditionalInfo(additionalInfo)	;

	msg.setCommand( com.cannontech.loadcontrol.messages.LMManualControlRequest.SCHEDULED_STOP );
	msg.setStartPriority( getDefaultPriority().intValue() );
	
	msg.setYukonID( getYukonID().intValue() );

	return msg;
}
/**
 * Insert the method's description here.
 * Creation date: (7/19/2001 8:50:05 AM)
 */
public com.cannontech.loadcontrol.messages.LMManualControlRequest createStartStopNowMsg( java.util.Date stopTime, int gearNumber, String additionalInfo, boolean isStart )
{
	com.cannontech.loadcontrol.messages.LMManualControlRequest msg = new com.cannontech.loadcontrol.messages.LMManualControlRequest();	
	java.util.GregorianCalendar cStop = new java.util.GregorianCalendar();
	cStop.setTime(stopTime);

	
	msg.setStartGear( gearNumber );

	if( stopTime != null )
		msg.setStopTime(cStop);

	if( additionalInfo != null )
		msg.setAddditionalInfo(additionalInfo)	;

	msg.setYukonID( getYukonID().intValue() );
	msg.setStartPriority( getDefaultPriority().intValue() );

	if( isStart )
		msg.setCommand( com.cannontech.loadcontrol.messages.LMManualControlRequest.START_NOW );
	else
		msg.setCommand( com.cannontech.loadcontrol.messages.LMManualControlRequest.STOP_NOW );

	return msg;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getAckTimeLimit() {
	return ackTimeLimit;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getActionDateTime() {
	return actionDateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @return java.lang.String
 */
public java.lang.String getAdditionalInfo() {
	return additionalInfo;
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2001 10:03:52 AM)
 * @return java.lang.String
 */
public java.lang.String getCanceledMsg() {
	return canceledMsg;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getCurtailmentStartTime() {
	return curtailmentStartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getCurtailmentStopTime() {
	return curtailmentStopTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurtailReferenceId() {
	return curtailReferenceId;
}
/**
 * Creation date: (6/26/2001 11:35:18 AM)
 * @param hours int
 */
public int getDuration() 
{
	return (int) 
		   ((getCurtailmentStopTime().getTime().getTime() - getCurtailmentStartTime().getTime().getTime()) / (60L * 1000L));
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @return java.lang.String
 */
public java.lang.String getHeading() {
	return heading;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @return java.lang.String
 */
public java.lang.String getMessageFooter() {
	return messageFooter;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @return java.lang.String
 */
public java.lang.String getMessageHeader() {
	return messageHeader;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMinNotifyTime() {
	return minNotifyTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getNotificationDateTime() {
	return notificationDateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @return java.lang.String
 */
public java.lang.String getRunStatus() {
	return runStatus;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @return java.lang.String
 */
public java.util.GregorianCalendar getStartTime()
{
	return getCurtailmentStartTime();
}
/**
 * Insert the method's description here.
 * Creation date: (4/23/2001 1:28:12 PM)
 * @return java.util.GregorianCalendar
 */
//Overrides our supers getStoppedControlling()
public java.util.GregorianCalendar getStoppedControlling() 
{
	return getCurtailmentStopTime();
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2001 10:03:52 AM)
 * @return java.lang.String
 */
public java.lang.String getStoppedEarlyMsg() {
	return stoppedEarlyMsg;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @return java.lang.String
 */
public java.util.GregorianCalendar getStopTime()
{
	return getCurtailmentStopTime();
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @param newAckTimeLimit java.lang.Integer
 */
public void setAckTimeLimit(java.lang.Integer newAckTimeLimit) {
	ackTimeLimit = newAckTimeLimit;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @param newActionDateTime java.util.GregorianCalendar
 */
public void setActionDateTime(java.util.GregorianCalendar newActionDateTime) {
	actionDateTime = newActionDateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @param newAdditionalInfo java.lang.String
 */
public void setAdditionalInfo(java.lang.String newAdditionalInfo) {
	additionalInfo = newAdditionalInfo;
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2001 10:03:52 AM)
 * @param newCanceledMsg java.lang.String
 */
public void setCanceledMsg(java.lang.String newCanceledMsg) {
	canceledMsg = newCanceledMsg;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @param newCurtailmentStartTime java.util.GregorianCalendar
 */
public void setCurtailmentStartTime(java.util.GregorianCalendar newCurtailmentStartTime) {
	curtailmentStartTime = newCurtailmentStartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @param newCurtailmentStopTime java.util.GregorianCalendar
 */
public void setCurtailmentStopTime(java.util.GregorianCalendar newCurtailmentStopTime) {
	curtailmentStopTime = newCurtailmentStopTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @param newCurtailReferenceId java.lang.Integer
 */
public void setCurtailReferenceId(java.lang.Integer newCurtailReferenceId) {
	curtailReferenceId = newCurtailReferenceId;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @param newHeading java.lang.String
 */
public void setHeading(java.lang.String newHeading) {
	heading = newHeading;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @param newMessageFooter java.lang.String
 */
public void setMessageFooter(java.lang.String newMessageFooter) {
	messageFooter = newMessageFooter;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @param newMessageHeader java.lang.String
 */
public void setMessageHeader(java.lang.String newMessageHeader) {
	messageHeader = newMessageHeader;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @param newMinNotifyTime java.lang.Integer
 */
public void setMinNotifyTime(java.lang.Integer newMinNotifyTime) {
	minNotifyTime = newMinNotifyTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @param newNotificationDateTime java.util.GregorianCalendar
 */
public void setNotificationDateTime(java.util.GregorianCalendar newNotificationDateTime) {
	notificationDateTime = newNotificationDateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @param newRunStatus java.lang.String
 */
public void setRunStatus(java.lang.String newRunStatus) {
	runStatus = newRunStatus;
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2001 10:03:52 AM)
 * @param newStoppedEarlyMsg java.lang.String
 */
public void setStoppedEarlyMsg(java.lang.String newStoppedEarlyMsg) {
	stoppedEarlyMsg = newStoppedEarlyMsg;
}
}
