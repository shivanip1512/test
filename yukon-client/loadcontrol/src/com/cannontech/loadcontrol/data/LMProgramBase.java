package com.cannontech.loadcontrol.data;

/**
 * Insert the type's description here.
 * Creation date: (8/17/00 3:06:09 PM)
 * @author: 
 */
public abstract class LMProgramBase implements ILMProgramMessageCreation, ILMData
{
	//constants that must match values in lmprogrambase.h/lmprogrambase.cpp
	//control types
	public static final String CONTROL_AUTOMATIC = "Automatic";
	public static final String CONTROL_TIMED = "Timed";
	public static final String CONTROL_MANUAL = "ManualOnly";

	//status of a LMProgramBase, programStatus values
	public static final int STATUS_INACTIVE = 0; //start only
	public static final int STATUS_ACTIVE = 1; //active
	public static final int STATUS_MANUAL_ACTIVE = 2; //active
	public static final int STATUS_SCHEDULED = 3; //scheduled
	public static final int STATUS_NOTIFIED = 4;  //notified
	public static final int STATUS_FULL_ACTIVE = 5;
	public static final int STATUS_STOPPING = 6;
	public static final int STATUS_CNTRL_ATTEMPT = 7;	
	public static final int STATUS_NON_CNTRL = 8;
	public static final int STATUS_TIMED_ACTIVE = 9;
	
	private Integer yukonID = null;
	private String yukonCategory = null;
	private String yukonClass = null;
	private String yukonName = null;
	private Integer yukonType = null;
	private String yukonDescription = null;
	private Boolean disableFlag = null;
	private Integer userOrder = null;
	private Integer stopOrder = null;
	private Integer defaultPriority = null;
	private String controlType = null;
	private String availableWeekDays = null;
	private Integer maxHoursDaily = null;
	private Integer maxHoursMonthly = null;
	private Integer maxHoursSeasonal = null;
	private Integer maxHoursAnnually = null;
	private Integer minActivateTime = null;
	private Integer minResponseTime = null;
	private Integer programStatusPointID = null;
	private Integer programStatus = null;
	private Integer reductionAnalogPointId = null;
	private Double reductionTotal = null;
	private java.util.GregorianCalendar startedControlling = null;
	private java.util.GregorianCalendar lastControlSent = null;
	private Boolean manualControlReceivedFlag = null;

	private java.util.Vector controlWindowVector = null;

	//contains objects of type ILMGroup
	private java.util.Vector loadControlGroupVector = null;

	//data not resotred when sent/received
	private java.util.GregorianCalendar stoppedControlling = null;
/**
 * Creation date: (6/26/2001 1:52:39 PM)
 * @return boolean
 * @param o java.lang.Object
 */
public boolean equals(Object o) 
{
	return ( (o != null) &&
			 (o instanceof LMProgramBase) &&
		     ((LMProgramBase) o).getYukonID().equals(getYukonID()) );
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @return java.lang.String
 */
public java.lang.String getAvailableWeekDays() {
	return availableWeekDays;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @return java.lang.String
 */
public java.lang.String getControlType() {
	return controlType;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @return java.util.Vector
 */
public java.util.Vector getControlWindowVector() {
	return controlWindowVector;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDefaultPriority() {
	return defaultPriority;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @return java.lang.Boolean
 */
public java.lang.Boolean getDisableFlag() {
	return disableFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getLastControlSent() {
	return lastControlSent;
}
/**
 * Insert the method's description here.
 * Creation date: (4/10/2001 10:23:43 AM)
 * @return java.util.Vector
 */
public java.util.Vector getLoadControlGroupVector() 
{
	if( loadControlGroupVector == null )
		loadControlGroupVector = new java.util.Vector(10);

	return loadControlGroupVector;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @return java.lang.Boolean
 */
public java.lang.Boolean getManualControlReceivedFlag() {
	return manualControlReceivedFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMaxHoursAnnually() {
	return maxHoursAnnually;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMaxHoursDaily() {
	return maxHoursDaily;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMaxHoursMonthly() {
	return maxHoursMonthly;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMaxHoursSeasonal() {
	return maxHoursSeasonal;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMinActivateTime() {
	return minActivateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMinResponseTime() {
	return minResponseTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/16/2001 12:36:21 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getProgramStatus() {
	return programStatus;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getProgramStatusPointID() {
	return programStatusPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/16/2001 9:49:04 AM)
 * @return java.lang.String
 * @param state int
 */
public static String getProgramStatusString(int status) 
{
	
	switch( status )
	{
		case STATUS_INACTIVE:
		return "Inactive";
		
		case STATUS_ACTIVE:
		return "Active";

		case STATUS_MANUAL_ACTIVE:
		return "Manual Active";

		case STATUS_SCHEDULED:
		return "Scheduled";

		case STATUS_NOTIFIED:
		return "Notified";

		case STATUS_FULL_ACTIVE:
		return "Full Active";

		case STATUS_NON_CNTRL:
		return "Control Attempt";
		
		case STATUS_STOPPING:
		return "Stopping";
		
		case STATUS_TIMED_ACTIVE:
		return "Timed Active";
		
		default:
		throw new RuntimeException("*** Unknown status(" + status + ") in getProgramStatusString(int) in : " + LMProgramBase.class.getName() );
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getReductionAnalogPointId() {
	return reductionAnalogPointId;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @return java.lang.Double
 */
public java.lang.Double getReductionTotal() {
	return reductionTotal;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getStartedControlling() {
	return startedControlling;
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2001 10:43:17 AM)
 * @return java.util.GregorianCalendar
 */
public abstract java.util.GregorianCalendar getStartTime();
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getStopOrder() {
	return stopOrder;
}
/**
 * Insert the method's description here.
 * Creation date: (4/23/2001 1:29:50 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getStoppedControlling() {
	return stoppedControlling;
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2001 10:43:17 AM)
 * @return java.util.GregorianCalendar
 */
public abstract java.util.GregorianCalendar getStopTime();
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getUserOrder() {
	return userOrder;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:44:53 AM)
 * @return java.lang.String
 */
public java.lang.String getYukonCategory() {
	return yukonCategory;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:44:53 AM)
 * @return java.lang.String
 */
public java.lang.String getYukonClass() {
	return yukonClass;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:44:53 AM)
 * @return java.lang.String
 */
public java.lang.String getYukonDescription() {
	return yukonDescription;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:44:53 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getYukonID() {
	return yukonID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:44:53 AM)
 * @return java.lang.String
 */
public java.lang.String getYukonName() {
	return yukonName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:44:53 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getYukonType() {
	return yukonType;
}
/**
 * Creation date: (6/26/2001 2:42:53 PM)
 * @return int
 */
public int hashCode() {
	return getYukonID().intValue();
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @param newAvailableWeekDays java.lang.String
 */
public void setAvailableWeekDays(java.lang.String newAvailableWeekDays) {
	availableWeekDays = newAvailableWeekDays;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @param newControlType java.lang.String
 */
public void setControlType(java.lang.String newControlType) {
	controlType = newControlType;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @param newControlWindowVector java.util.Vector
 */
public void setControlWindowVector(java.util.Vector newControlWindowVector) {
	controlWindowVector = newControlWindowVector;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @param newDefaultPriority java.lang.Integer
 */
public void setDefaultPriority(java.lang.Integer newDefaultPriority) {
	defaultPriority = newDefaultPriority;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @param newDisableFlag java.lang.Boolean
 */
public void setDisableFlag(java.lang.Boolean newDisableFlag) {
	disableFlag = newDisableFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @param newLastControlSent java.util.GregorianCalendar
 */
public void setLastControlSent(java.util.GregorianCalendar newLastControlSent) {
	lastControlSent = newLastControlSent;
}
/**
 * Insert the method's description here.
 * Creation date: (4/10/2001 10:23:43 AM)
 * @param newLoadControlGroupVector java.util.Vector
 */
public void setLoadControlGroupVector(java.util.Vector newLoadControlGroupVector) {
	loadControlGroupVector = newLoadControlGroupVector;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @param newManualControlReceivedFlag java.lang.Boolean
 */
public void setManualControlReceivedFlag(java.lang.Boolean newManualControlReceivedFlag) {
	manualControlReceivedFlag = newManualControlReceivedFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @param newMaxHoursAnnually java.lang.Integer
 */
public void setMaxHoursAnnually(java.lang.Integer newMaxHoursAnnually) {
	maxHoursAnnually = newMaxHoursAnnually;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @param newMaxHoursDaily java.lang.Integer
 */
public void setMaxHoursDaily(java.lang.Integer newMaxHoursDaily) {
	maxHoursDaily = newMaxHoursDaily;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @param newMaxHoursMonthly java.lang.Integer
 */
public void setMaxHoursMonthly(java.lang.Integer newMaxHoursMonthly) {
	maxHoursMonthly = newMaxHoursMonthly;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @param newMaxHoursSeasonal java.lang.Integer
 */
public void setMaxHoursSeasonal(java.lang.Integer newMaxHoursSeasonal) {
	maxHoursSeasonal = newMaxHoursSeasonal;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @param newMinActivateTime java.lang.Integer
 */
public void setMinActivateTime(java.lang.Integer newMinActivateTime) {
	minActivateTime = newMinActivateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @param newMinResponseTime java.lang.Integer
 */
public void setMinResponseTime(java.lang.Integer newMinResponseTime) {
	minResponseTime = newMinResponseTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/16/2001 12:36:21 PM)
 * @param newProgramStatus java.lang.Integer
 */
public void setProgramStatus(java.lang.Integer newProgramStatus) {
	programStatus = newProgramStatus;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @param newProgramStatusPointID java.lang.Integer
 */
public void setProgramStatusPointID(java.lang.Integer newProgramStatusPointID) {
	programStatusPointID = newProgramStatusPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @param newReductionAnalogPointId java.lang.Integer
 */
public void setReductionAnalogPointId(java.lang.Integer newReductionAnalogPointId) {
	reductionAnalogPointId = newReductionAnalogPointId;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @param newReductionTotal java.lang.Double
 */
public void setReductionTotal(java.lang.Double newReductionTotal) {
	reductionTotal = newReductionTotal;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @param newStartedControlling java.util.GregorianCalendar
 */
public void setStartedControlling(java.util.GregorianCalendar newStartedControlling) {
	startedControlling = newStartedControlling;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @param newStopOrder java.lang.Integer
 */
public void setStopOrder(java.lang.Integer newStopOrder) {
	stopOrder = newStopOrder;
}
/**
 * Insert the method's description here.
 * Creation date: (4/23/2001 1:29:50 PM)
 * @param newStoppedControlling java.util.GregorianCalendar
 */
public void setStoppedControlling(java.util.GregorianCalendar newStoppedControlling) {
	stoppedControlling = newStoppedControlling;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:58:13 AM)
 * @param newUserOrder java.lang.Integer
 */
public void setUserOrder(java.lang.Integer newUserOrder) {
	userOrder = newUserOrder;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:44:53 AM)
 * @param newYukonCategory java.lang.String
 */
public void setYukonCategory(java.lang.String newYukonCategory) {
	yukonCategory = newYukonCategory;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:44:53 AM)
 * @param newYukonClass java.lang.String
 */
public void setYukonClass(java.lang.String newYukonClass) {
	yukonClass = newYukonClass;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:44:53 AM)
 * @param newYukonDescription java.lang.String
 */
public void setYukonDescription(java.lang.String newYukonDescription) {
	yukonDescription = newYukonDescription;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:44:53 AM)
 * @param newYukonID java.lang.Integer
 */
public void setYukonID(java.lang.Integer newYukonID) {
	yukonID = newYukonID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:44:53 AM)
 * @param newYukonName java.lang.String
 */
public void setYukonName(java.lang.String newYukonName) {
	yukonName = newYukonName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:44:53 AM)
 * @param newYukonType java.lang.Integer
 */
public void setYukonType(java.lang.Integer newYukonType) {
	yukonType = newYukonType;
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/2001 11:46:24 AM)
 * @return java.lang.String
 */
public String toString() {
	return getYukonName();
}
}
