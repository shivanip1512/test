package com.cannontech.message.macs.message;

import com.cannontech.database.data.schedule.script.ScriptTemplateTypes;

/**
 * This type was created in VisualAge.
 */

public class Schedule extends com.cannontech.message.util.Message implements java.io.Serializable
{
	// String constants that represent the various
	// last run status's
	public static final String LAST_STATUS_NONE = "None";
	public static final String LAST_STATUS_ERROR = "Error";
	public static final String LAST_STATUS_FINISHED = "Finished";

	public static final String[] LAST_RUN_STATUS =
	{
		LAST_STATUS_NONE,
		LAST_STATUS_ERROR,
		LAST_STATUS_FINISHED
	};
	
	// String constants that represent the various
	// states a schedule can be in
	public static final String STATE_RUNNING = "Running";
	public static final String STATE_WAITING = "Waiting";
	public static final String STATE_PENDING = "Pending";
	public static final String STATE_DISABLED = "Disabled";
	
	// An invalid time for any schedule date field being the Year 1900 in millis
	public static final long INVALID_DATE = -2177452800000L;

	private int id = 0;
	private String scheduleName = "";
	private String categoryName = "";
	private String type = "";
	private int holidayScheduleId = 0;
	private String scriptFileName = "";
	private String currentState = STATE_WAITING;
	private String startPolicy = "";
	private String stopPolicy = "";
	private java.util.Date lastRunTime = new java.util.Date(INVALID_DATE);
	private String lastRunStatus = LAST_STATUS_NONE;
	private int startDay = 0;
	private int startMonth = 0;
	private int startYear = 0;
	private String startTime = "00:00:00"; // must be 8 chars always!!
	private String stopTime = "00:00:00";  // must be 8 chars always!!
	private String validWeekDays = "YYYYYYYN";
	private int duration = 0;
	private java.util.Date manualStartTime = new java.util.Date(INVALID_DATE);
	private java.util.Date manualStopTime = new java.util.Date(INVALID_DATE);
	private String targetSelect = "";
	private String startCommand = "";
	private String stopCommand = "";
	private int repeatInterval = 0;
	private int templateType = ScriptTemplateTypes.NO_TEMPLATE_SCRIPT;
	
	// these do not get restored on the C++ side
	private java.util.Date nextRunTime = null;
	private java.util.Date nextStopTime = null;

	// data that is not stored in the database and does not get restored
	private transient NonPersistantScheduleData nonPersistantData = null;

	// Schedule types
	public static final String SIMPLE_TYPE = "Simple";
	public static final String SCRIPT_TYPE = "Script";

	// Start Policies
	public static final String DATETIME_START = "DateTime";
	public static final String DAYOFMONTH_START = "DayOfMonthTime";
	public static final String WEEKDAY_START = "WeekDayTime";
	public static final String MANUAL_START = "Manual";

	// Stop Policies
	public static final String UNTILCOMPLETE_STOP = "UntilComplete";
	public static final String ABSOLUTETIME_STOP = "AbsoluteTime";
	public static final String DURATION_STOP = "Duration";
	public static final String MANUAL_STOP = "Manual";

/**
 * Schedule constructor comment.
 */
public Schedule() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/20/00 11:20:32 AM)
 * @return boolean
 * @param val java.lang.Object
 */
public boolean equals(Object val) {

	if( val instanceof Schedule )
	{
		return (getId() == ((Schedule) val).getId());
	}
	else
		return super.equals(val);
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @return java.lang.String
 */
public java.lang.String getCategoryName() {
	return categoryName;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @return java.lang.String
 */
public java.lang.String getCurrentState() {
	return currentState;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @return int
 */
public int getDuration() {
	return duration;
}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2001 1:45:29 PM)
 * @return int
 */
public int getHolidayScheduleId() {
	return holidayScheduleId;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @return int
 */
public int getId() {
	return id;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @return java.lang.String
 */
public java.lang.String getLastRunStatus() {
	return lastRunStatus;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @return java.util.Date
 */
public java.util.Date getLastRunTime() {
	return lastRunTime;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @return java.util.Date
 */
public java.util.Date getManualStartTime() {
	return manualStartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @return java.util.Date
 */
public java.util.Date getManualStopTime() {
	return manualStopTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/8/2001 2:05:07 PM)
 * @return java.util.Date
 */
public java.util.Date getNextRunTime() {
	return nextRunTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/8/2001 2:05:07 PM)
 * @return java.util.Date
 */
public java.util.Date getNextStopTime() {
	return nextStopTime;
}

/**
 * Insert the method's description here.
 * Creation date: (3/9/2001 3:34:23 PM)
 * @return com.cannontech.macs.NonPersistantScheduleData
 */
public NonPersistantScheduleData getNonPersistantData() 
{
	if( nonPersistantData == null )
		nonPersistantData = new NonPersistantScheduleData();

	return nonPersistantData;
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2001 4:44:06 PM)
 * @return int
 */
public int getRepeatInterval() {
	return repeatInterval;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @return java.lang.String
 */
public java.lang.String getScheduleName() {
	return scheduleName;
}
/**
 * Insert the method's description here.
 * Creation date: (3/1/2001 1:10:40 PM)
 * @return java.lang.String
 */
public java.lang.String getScriptFileName() {
	return scriptFileName;
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2001 4:44:06 PM)
 * @return java.lang.String
 */
public java.lang.String getStartCommand() {
	return startCommand;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @return int
 */
public int getStartDay() {
	return startDay;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @return int
 */
public int getStartMonth() {
	return startMonth;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @return java.lang.String
 */
public java.lang.String getStartPolicy() {
	return startPolicy;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 */
public String getStartTime() {
	return startTime;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @return int
 */
public int getStartYear() {
	return startYear;
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2001 4:44:06 PM)
 * @return java.lang.String
 */
public java.lang.String getStopCommand() {
	return stopCommand;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @return java.lang.String
 */
public java.lang.String getStopPolicy() {
	return stopPolicy;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 */
public String getStopTime() {
	return stopTime;
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2001 4:44:06 PM)
 * @return java.lang.String
 */
public java.lang.String getTargetSelect() {
	return targetSelect;
}
/**
 * @return int
 */
public int getTemplateType(){
    return templateType;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @return java.lang.String
 */
public java.lang.String getType() {
	return type;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @return java.lang.String
 */
public java.lang.String getValidWeekDays() {
	return validWeekDays;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @param newCategoryName java.lang.String
 */
public void setCategoryName(java.lang.String newCategoryName) {
	categoryName = newCategoryName;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @param newCurrentState java.lang.String
 */
public void setCurrentState(java.lang.String newCurrentState) {
	currentState = newCurrentState;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @param newDuration int
 */
public void setDuration(int newDuration) {
	duration = newDuration;
}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2001 1:45:29 PM)
 * @param newHolidayScheduleId int
 */
public void setHolidayScheduleId(int newHolidayScheduleId) {
	holidayScheduleId = newHolidayScheduleId;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @param newId int
 */
public void setId(int newId) {
	id = newId;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @param newLastRunStatus java.lang.String
 */
public void setLastRunStatus(java.lang.String newLastRunStatus) {
	lastRunStatus = newLastRunStatus;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @param newLastRunTime java.util.Date
 */
public void setLastRunTime(java.util.Date newLastRunTime) {
	lastRunTime = newLastRunTime;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @param newManualStartTime java.util.Date
 */
public void setManualStartTime(java.util.Date newManualStartTime) {
	manualStartTime = newManualStartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @param newManualStopTime java.util.Date
 */
public void setManualStopTime(java.util.Date newManualStopTime) {
	manualStopTime = newManualStopTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/8/2001 2:05:07 PM)
 * @param newNextRunTime java.util.Date
 */
public void setNextRunTime(java.util.Date newNextRunTime) {
	nextRunTime = newNextRunTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/8/2001 2:05:07 PM)
 * @param newNextStopTime java.util.Date
 */
public void setNextStopTime(java.util.Date newNextStopTime) {
	nextStopTime = newNextStopTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/9/2001 3:34:23 PM)
 * @param newNonPersistantData com.cannontech.macs.NonPersistantScheduleData
 */
public void setNonPersistantData(NonPersistantScheduleData newNonPersistantData) {
	nonPersistantData = newNonPersistantData;
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2001 4:44:06 PM)
 * @param newRepeatInterval int
 */
public void setRepeatInterval(int newRepeatInterval) {
	repeatInterval = newRepeatInterval;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @param newScheduleName java.lang.String
 */
public void setScheduleName(java.lang.String newScheduleName) {
	scheduleName = newScheduleName;
}
/**
 * Insert the method's description here.
 * Creation date: (3/1/2001 1:10:40 PM)
 * @param newScriptFileName java.lang.String
 */
public void setScriptFileName(java.lang.String newScriptFileName) 
{
	scriptFileName = newScriptFileName;
	getNonPersistantData().getScript().setFileName(newScriptFileName);
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2001 4:44:06 PM)
 * @param newStartCommand java.lang.String
 */
public void setStartCommand(java.lang.String newStartCommand) {
	startCommand = newStartCommand;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @param newStartDay int
 */
public void setStartDay(int newStartDay) {
	startDay = newStartDay;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @param newStartMonth int
 */
public void setStartMonth(int newStartMonth) {
	startMonth = newStartMonth;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @param newStartPolicy java.lang.String
 */
public void setStartPolicy(java.lang.String newStartPolicy) {
	startPolicy = newStartPolicy;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 */
public void setStartTime(String newStartTime) {
	startTime = newStartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @param newStartYear int
 */
public void setStartYear(int newStartYear) {
	startYear = newStartYear;
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2001 4:44:06 PM)
 * @param newStopCommand java.lang.String
 */
public void setStopCommand(java.lang.String newStopCommand) {
	stopCommand = newStopCommand;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @param newStopPolicy java.lang.String
 */
public void setStopPolicy(java.lang.String newStopPolicy) {
	stopPolicy = newStopPolicy;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 */
public void setStopTime(String newStopTime) {
	stopTime = newStopTime;
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2001 4:44:06 PM)
 * @param newTargetSelect java.lang.String
 */
public void setTargetSelect(java.lang.String newTargetSelect) {
	targetSelect = newTargetSelect;
}
/**
 * @param newTemplateType
 */
public void setTemplateType(int newTemplateType){
    templateType = newTemplateType ;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @param newType java.lang.String
 */
public void setType(java.lang.String newType) {
	type = newType;
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 2:53:32 PM)
 * @param newValidWeekDays java.lang.String
 */
public void setValidWeekDays(java.lang.String newValidWeekDays) {
	validWeekDays = newValidWeekDays;
}
}
