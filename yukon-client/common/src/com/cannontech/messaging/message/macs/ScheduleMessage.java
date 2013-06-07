package com.cannontech.messaging.message.macs;

import com.cannontech.database.data.schedule.script.ScriptTemplateTypes;
import com.cannontech.messaging.message.BaseMessage;

public class ScheduleMessage extends BaseMessage implements java.io.Serializable {

    // String constants that represent the various
    // last run status's
    public static final String LAST_STATUS_NONE = "None";
    public static final String LAST_STATUS_ERROR = "Error";
    public static final String LAST_STATUS_FINISHED = "Finished";

    public static final String[] LAST_RUN_STATUS = { LAST_STATUS_NONE, LAST_STATUS_ERROR, LAST_STATUS_FINISHED };

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
    private boolean updatingState = false;
    private String startPolicy = "";
    private String stopPolicy = "";
    private java.util.Date lastRunTime = new java.util.Date(INVALID_DATE);
    private String lastRunStatus = LAST_STATUS_NONE;
    private int startDay = 0;
    private int startMonth = 0;
    private int startYear = 0;
    private String startTime = "00:00:00"; // must be 8 chars always!!
    private String stopTime = "00:00:00"; // must be 8 chars always!!
    private String validWeekDays = "YYYYYYYN";
    private int duration = 0;
    private java.util.Date manualStartTime = new java.util.Date(INVALID_DATE);
    private java.util.Date manualStopTime = new java.util.Date(INVALID_DATE);
    private int targetPAObjectId = 0;
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

    public boolean equals(Object val) {

        if (val instanceof ScheduleMessage) {
            return (getId() == ((ScheduleMessage) val).getId());
        }
        else
            return super.equals(val);
    }

    public java.lang.String getCategoryName() {
        return categoryName;
    }

    public java.lang.String getCurrentState() {
        return currentState;
    }

    public int getDuration() {
        return duration;
    }

    public int getHolidayScheduleId() {
        return holidayScheduleId;
    }

    public int getId() {
        return id;
    }

    public java.lang.String getLastRunStatus() {
        return lastRunStatus;
    }

    public java.util.Date getLastRunTime() {
        return lastRunTime;
    }

    public java.util.Date getManualStartTime() {
        return manualStartTime;
    }

    public java.util.Date getManualStopTime() {
        return manualStopTime;
    }

    public java.util.Date getNextRunTime() {
        return nextRunTime;
    }

    public java.util.Date getNextStopTime() {
        return nextStopTime;
    }

    public NonPersistantScheduleData getNonPersistantData() {
        if (nonPersistantData == null)
            nonPersistantData = new NonPersistantScheduleData();

        return nonPersistantData;
    }

    public int getRepeatInterval() {
        return repeatInterval;
    }

    public java.lang.String getScheduleName() {
        return scheduleName;
    }

    public java.lang.String getScriptFileName() {
        return scriptFileName;
    }

    public java.lang.String getStartCommand() {
        return startCommand;
    }

    public int getStartDay() {
        return startDay;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public java.lang.String getStartPolicy() {
        return startPolicy;
    }

    public String getStartTime() {
        return startTime;
    }

    public int getStartYear() {
        return startYear;
    }

    public java.lang.String getStopCommand() {
        return stopCommand;
    }

    public java.lang.String getStopPolicy() {
        return stopPolicy;
    }

    public String getStopTime() {
        return stopTime;
    }

    public int getTargetPAObjectId() {
        return targetPAObjectId;
    }

    public int getTemplateType() {
        return templateType;
    }

    public java.lang.String getType() {
        return type;
    }

    public java.lang.String getValidWeekDays() {
        return validWeekDays;
    }

    public void setCategoryName(java.lang.String newCategoryName) {
        categoryName = newCategoryName;
    }

    public void setCurrentState(java.lang.String newCurrentState) {
        currentState = newCurrentState;
    }

    public void setDuration(int newDuration) {
        duration = newDuration;
    }

    public void setHolidayScheduleId(int newHolidayScheduleId) {
        holidayScheduleId = newHolidayScheduleId;
    }

    public void setId(int newId) {
        id = newId;
    }

    public void setLastRunStatus(java.lang.String newLastRunStatus) {
        lastRunStatus = newLastRunStatus;
    }

    public void setLastRunTime(java.util.Date newLastRunTime) {
        lastRunTime = newLastRunTime;
    }

    public void setManualStartTime(java.util.Date newManualStartTime) {
        manualStartTime = newManualStartTime;
    }

    public void setManualStopTime(java.util.Date newManualStopTime) {
        manualStopTime = newManualStopTime;
    }

    public void setNextRunTime(java.util.Date newNextRunTime) {
        nextRunTime = newNextRunTime;
    }

    public void setNextStopTime(java.util.Date newNextStopTime) {
        nextStopTime = newNextStopTime;
    }

    public void setNonPersistantData(NonPersistantScheduleData newNonPersistantData) {
        nonPersistantData = newNonPersistantData;
    }

    public void setRepeatInterval(int newRepeatInterval) {
        repeatInterval = newRepeatInterval;
    }

    public void setScheduleName(java.lang.String newScheduleName) {
        scheduleName = newScheduleName;
    }

    public void setScriptFileName(java.lang.String newScriptFileName) {
        scriptFileName = newScriptFileName;
        getNonPersistantData().getScript().setFileName(newScriptFileName);
    }

    public void setStartCommand(java.lang.String newStartCommand) {
        startCommand = newStartCommand;
    }

    public void setStartDay(int newStartDay) {
        startDay = newStartDay;
    }

    public void setStartMonth(int newStartMonth) {
        startMonth = newStartMonth;
    }

    public void setStartPolicy(java.lang.String newStartPolicy) {
        startPolicy = newStartPolicy;
    }

    public void setStartTime(String newStartTime) {
        startTime = newStartTime;
    }

    public void setStartYear(int newStartYear) {
        startYear = newStartYear;
    }

    public void setStopCommand(java.lang.String newStopCommand) {
        stopCommand = newStopCommand;
    }

    public void setStopPolicy(java.lang.String newStopPolicy) {
        stopPolicy = newStopPolicy;
    }

    public void setStopTime(String newStopTime) {
        stopTime = newStopTime;
    }

    public void setTargetPAObjectId(int targetPAObjectId) {
        this.targetPAObjectId = targetPAObjectId;
    }

    public void setTemplateType(int newTemplateType) {
        templateType = newTemplateType;
    }

    public void setType(java.lang.String newType) {
        type = newType;
    }

    public void setValidWeekDays(java.lang.String newValidWeekDays) {
        validWeekDays = newValidWeekDays;
    }

    public boolean getUpdatingState() {
        return updatingState;
    }

    public void setUpdatingState(boolean updatingState) {
        this.updatingState = updatingState;
    }
}
