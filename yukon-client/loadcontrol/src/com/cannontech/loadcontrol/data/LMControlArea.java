package com.cannontech.loadcontrol.data;

/**
 * Insert the type's description here.
 * Creation date: (8/17/00 3:06:09 PM)
 * @author: 
 */
public class LMControlArea implements ILMData
{
	public static final int INVAID_INT = -1;
	
	public static final int STATE_INACTIVE = 0;
	public static final int STATE_ACTIVE = 1;
	public static final int STATE_MANUAL_ACTIVE = 2;
	/* The SCHEDULED state is never set */
//	public static final int STATE_SCHEDULED = 3;
	public static final int STATE_FULLY_ACTIVE = 4;
	public static final int STATE_CNTRL_ATTEMPT = 5;

	private Integer yukonID = null;
	private String yukonCategory = null;
	private String yukonClass = null;
	private String yukonName = null;
	private Integer yukonType = null;
	private String yukonDescription = null;
	private Boolean disableFlag = null;
	private String defOperationalState = null;
	private Integer controlInterval = null;
	private Integer minResponseTime = null;
	private Integer defDailyStartTime = null;
	private Integer defDailyStopTime = null;
	private Boolean requireAllTriggersActiveFlag = null;
	private java.util.GregorianCalendar nextCheckTime = null;
	private Boolean newPointDataReceivedFlag = null;
	private Boolean updatedFlag = null;
	private Integer controlAreaStatusPointId = null;
	private Integer controlAreaState = null;
	private Integer currentPriority = null;
	private Integer currentDailyStartTime = null;
	private Integer currentDailyStopTime = null;
	
	//LMControlAreaTrigger Objects
	private java.util.Vector triggerVector = null;

	//LMProgramBase Objects
	private java.util.Vector lmProgramVector = null;

/**
 * Insert the method's description here.
 * Creation date: (3/20/00 11:20:32 AM)
 * @return boolean
 * @param val java.lang.Object
 */
public boolean equals(Object val) 
{
	if( val instanceof LMControlArea )
	{
		return (getYukonID().intValue() == ((LMControlArea) val).getYukonID().intValue());
	}
	else
		return super.equals(val);
}

public int hashCode() 
{
	return getYukonID().intValue();
}

/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getControlAreaState() {
	return controlAreaState;
}
/**
 * Insert the method's description here.
 * Creation date: (4/16/2001 10:50:18 AM)
 * @return java.lang.String
 * @param state int
 */
public static java.awt.Color getControlAreaStateColor( LMControlArea areaValue )
{
	if( areaValue.getDisableFlag().booleanValue() )
		return java.awt.Color.red;
	else if( areaValue.getControlAreaState().intValue() == LMControlArea.STATE_CNTRL_ATTEMPT )
			   //|| areaValue.getControlAreaState().intValue() == LMControlArea.STATE_SCHEDULED )
	{
		return java.awt.Color.yellow;
	}
	else if( areaValue.getControlAreaState().intValue() == LMControlArea.STATE_INACTIVE )
		return java.awt.Color.black;
	else
		return java.awt.Color.green.darker();
}
/**
 * Insert the method's description here.
 * Creation date: (4/16/2001 10:50:18 AM)
 * @return java.lang.String
 * @param state int
 */
public static String getControlAreaStateString(int state) 
{
	switch( state )
	{
		case STATE_INACTIVE:
		return "INACTIVE";
		
		case STATE_ACTIVE:
		return "ACTIVE";

		case STATE_MANUAL_ACTIVE:
		return "MANUAL ACTIVE";

//		case STATE_SCHEDULED:
//		return "SCHEDULED";

		case STATE_FULLY_ACTIVE:
		return "FULLY ACTIVE";

		case STATE_CNTRL_ATTEMPT:
		return "CONTROL ATTEMPT";
		
		default:
		throw new RuntimeException("*** Unknown state(" + state + ") in getControlAreaStateString(int) in : " + LMControlArea.class.getName() );
	}

}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getControlAreaStatusPointId() {
	return controlAreaStatusPointId;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getControlInterval() {
	return controlInterval;
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2002 3:38:33 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentDailyStartTime() {
	return currentDailyStartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2002 3:38:33 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentDailyStopTime() {
	return currentDailyStopTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentPriority() {
	return currentPriority;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDefDailyStartTime() {
	return defDailyStartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDefDailyStopTime() {
	return defDailyStopTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @return java.lang.String
 */
public java.lang.String getDefOperationalState() {
	return defOperationalState;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @return java.lang.Boolean
 */
public java.lang.Boolean getDisableFlag() {
	return disableFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @return java.util.Vector
 */
public java.util.Vector getLmProgramVector() 
{
	if( lmProgramVector == null )
		lmProgramVector = new java.util.Vector(10);

	return lmProgramVector;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMinResponseTime() {
	return minResponseTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @return java.lang.Boolean
 */
public java.lang.Boolean getNewPointDataReceivedFlag() {
	return newPointDataReceivedFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getNextCheckTime() {
	return nextCheckTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @return java.lang.Boolean
 */
public java.lang.Boolean getRequireAllTriggersActiveFlag() {
	return requireAllTriggersActiveFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @return java.util.Vector
 */
public java.util.Vector getTriggerVector() 
{
	if( triggerVector == null )
		triggerVector = new java.util.Vector(2);

	return triggerVector;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @return java.lang.Boolean
 */
public java.lang.Boolean getUpdatedFlag() {
	return updatedFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:35:17 AM)
 * @return java.lang.String
 */
public java.lang.String getYukonCategory() {
	return yukonCategory;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:35:17 AM)
 * @return java.lang.String
 */
public java.lang.String getYukonClass() {
	return yukonClass;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:35:17 AM)
 * @return java.lang.String
 */
public java.lang.String getYukonDescription() {
	return yukonDescription;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:35:17 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getYukonID() {
	return yukonID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:35:17 AM)
 * @return java.lang.String
 */
public java.lang.String getYukonName() {
	return yukonName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:35:17 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getYukonType() {
	return yukonType;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @param newControlAreaState java.lang.Integer
 */
public void setControlAreaState(java.lang.Integer newControlAreaState) {
	controlAreaState = newControlAreaState;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @param newControlAreaStatusPointId java.lang.Integer
 */
public void setControlAreaStatusPointId(java.lang.Integer newControlAreaStatusPointId) {
	controlAreaStatusPointId = newControlAreaStatusPointId;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @param newControlInterval java.lang.Integer
 */
public void setControlInterval(java.lang.Integer newControlInterval) {
	controlInterval = newControlInterval;
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2002 3:38:33 PM)
 * @param newCurrentDailyStartTime java.lang.Integer
 */
public void setCurrentDailyStartTime(java.lang.Integer newCurrentDailyStartTime) {
	currentDailyStartTime = newCurrentDailyStartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2002 3:38:33 PM)
 * @param newCurrentDailyStopTime java.lang.Integer
 */
public void setCurrentDailyStopTime(java.lang.Integer newCurrentDailyStopTime) {
	currentDailyStopTime = newCurrentDailyStopTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @param newCurrentPriority java.lang.Integer
 */
public void setCurrentPriority(java.lang.Integer newCurrentPriority) {
	currentPriority = newCurrentPriority;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @param newDefDailyStartTime java.lang.Integer
 */
public void setDefDailyStartTime(java.lang.Integer newDefDailyStartTime) {
	defDailyStartTime = newDefDailyStartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @param newDefDailyStopTime java.lang.Integer
 */
public void setDefDailyStopTime(java.lang.Integer newDefDailyStopTime) {
	defDailyStopTime = newDefDailyStopTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @param newDefOperationalState java.lang.String
 */
public void setDefOperationalState(java.lang.String newDefOperationalState) {
	defOperationalState = newDefOperationalState;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @param newDisableFlag java.lang.Boolean
 */
public void setDisableFlag(java.lang.Boolean newDisableFlag) {
	disableFlag = newDisableFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @param newLmProgramVector java.util.Vector
 */
public void setLmProgramVector(java.util.Vector newLmProgramVector) {
	lmProgramVector = newLmProgramVector;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @param newMinResponseTime java.lang.Integer
 */
public void setMinResponseTime(java.lang.Integer newMinResponseTime) {
	minResponseTime = newMinResponseTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @param newNewPointDataReceivedFlag java.lang.Boolean
 */
public void setNewPointDataReceivedFlag(java.lang.Boolean newNewPointDataReceivedFlag) {
	newPointDataReceivedFlag = newNewPointDataReceivedFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @param newNextCheckTime java.util.GregorianCalendar
 */
public void setNextCheckTime(java.util.GregorianCalendar newNextCheckTime) {
	nextCheckTime = newNextCheckTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @param newRequireAllTriggersActiveFlag java.lang.Boolean
 */
public void setRequireAllTriggersActiveFlag(java.lang.Boolean newRequireAllTriggersActiveFlag) {
	requireAllTriggersActiveFlag = newRequireAllTriggersActiveFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @param newTriggerVector java.util.Vector
 */
public void setTriggerVector(java.util.Vector newTriggerVector) {
	triggerVector = newTriggerVector;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:56:59 AM)
 * @param newUpdatedFlag java.lang.Boolean
 */
public void setUpdatedFlag(java.lang.Boolean newUpdatedFlag) {
	updatedFlag = newUpdatedFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:35:17 AM)
 * @param newYukonCategory java.lang.String
 */
public void setYukonCategory(java.lang.String newYukonCategory) {
	yukonCategory = newYukonCategory;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:35:17 AM)
 * @param newYukonClass java.lang.String
 */
public void setYukonClass(java.lang.String newYukonClass) {
	yukonClass = newYukonClass;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:35:17 AM)
 * @param newYukonDescription java.lang.String
 */
public void setYukonDescription(java.lang.String newYukonDescription) {
	yukonDescription = newYukonDescription;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:35:17 AM)
 * @param newYukonID java.lang.Integer
 */
public void setYukonID(java.lang.Integer newYukonID) {
	yukonID = newYukonID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:35:17 AM)
 * @param newYukonName java.lang.String
 */
public void setYukonName(java.lang.String newYukonName) {
	yukonName = newYukonName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:35:17 AM)
 * @param newYukonType java.lang.Integer
 */
public void setYukonType(java.lang.Integer newYukonType) {
	yukonType = newYukonType;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 10:05:37 AM)
 * @return java.lang.String
 */
public String toString()
{
	return getYukonName();
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:18:26 PM)
 * @param newValues com.cannontech.loadcontrol.data.LMControlArea
 */
/* SORT OF SIMILAR TO CLONE, BUT DOES NOT CREATE A NEW OBJECT */
public synchronized void updateAllValues(LMControlArea newValues) 
{
	if( newValues == null )
		return;
	
	synchronized( newValues )
	{	
		setControlAreaState( newValues.getControlAreaState() );
		setControlAreaStatusPointId( newValues.getControlAreaStatusPointId() );
		setControlInterval( newValues.getControlInterval() );
		setCurrentPriority( newValues.getCurrentPriority() );
		setDefDailyStartTime( newValues.getDefDailyStartTime() );
		setDefDailyStopTime( newValues.getDefDailyStopTime() );
		setDefOperationalState( newValues.getDefOperationalState() );
		setDisableFlag( newValues.getDisableFlag() );
		setLmProgramVector( newValues.getLmProgramVector() );
		setMinResponseTime( newValues.getMinResponseTime() );
		setNewPointDataReceivedFlag( newValues.getNewPointDataReceivedFlag() );
		setNextCheckTime( newValues.getNextCheckTime() );
		setRequireAllTriggersActiveFlag( newValues.getRequireAllTriggersActiveFlag() );
		setTriggerVector( newValues.getTriggerVector() );
		setUpdatedFlag( newValues.getUpdatedFlag() );
		setYukonCategory( newValues.getYukonCategory() );
		setYukonClass( newValues.getYukonClass() );
		setYukonDescription( newValues.getYukonDescription() );
		setYukonID( newValues.getYukonID() );
		setYukonName( newValues.getYukonName() );
		setYukonType( newValues.getYukonType() );
	}

}
}
