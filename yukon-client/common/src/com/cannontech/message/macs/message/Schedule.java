package com.cannontech.message.macs.message;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.schedule.script.ScriptTemplateTypes;
import com.cannontech.message.util.Message;

public class Schedule extends Message implements Serializable, YukonPao {
    // String constants that represent the various
    // last run status's
    public final static String LAST_STATUS_NONE = "None";
    public final static String LAST_STATUS_ERROR = "Error";
    public final static String LAST_STATUS_FINISHED = "Finished";

    public final static String[] LAST_RUN_STATUS = { LAST_STATUS_NONE, LAST_STATUS_ERROR, LAST_STATUS_FINISHED };

    // String constants that represent the various
    // states a schedule can be in
    public final static String STATE_RUNNING = "Running";
    public final static String STATE_WAITING = "Waiting";
    public final static String STATE_PENDING = "Pending";
    public final static String STATE_DISABLED = "Disabled";

    // An invalid time for any schedule date field being the Year 1900 in millis
    public final static long INVALID_DATE = -2177452800000L;

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
    private Date lastRunTime = new Date(INVALID_DATE);
    private String lastRunStatus = LAST_STATUS_NONE;
    private int startDay = 0;
    private int startMonth = 0;
    private int startYear = 0;
    private String startTime = "00:00:00"; // must be 8 chars always!!
    private String stopTime = "00:00:00"; // must be 8 chars always!!
    private String validWeekDays = "YYYYYYYN";
    private int duration = 0;
    private Date manualStartTime = new Date(INVALID_DATE);
    private Date manualStopTime = new Date(INVALID_DATE);
    private int targetPAObjectId = 0;
    private String startCommand = "";
    private String stopCommand = "";
    private int repeatInterval = 0;
    private int templateType = ScriptTemplateTypes.NO_TEMPLATE_SCRIPT;

    // these do not get restored on the C++ side
    private Date nextRunTime = null;
    private Date nextStopTime = null;

    // data that is not stored in the database and does not get restored
    private transient NonPersistantScheduleData nonPersistantData = null;

    // Schedule types
    public final static String SIMPLE_TYPE = "Simple";
    public final static String SCRIPT_TYPE = "Script";

    // Start Policies
    public final static String DATETIME_START = "DateTime";
    public final static String DAYOFMONTH_START = "DayOfMonthTime";
    public final static String WEEKDAY_START = "WeekDayTime";
    public final static String MANUAL_START = "Manual";

    // Stop Policies
    public final static String UNTILCOMPLETE_STOP = "UntilComplete";
    public final static String ABSOLUTETIME_STOP = "AbsoluteTime";
    public final static String DURATION_STOP = "Duration";
    public final static String MANUAL_STOP = "Manual";

    @Override
    public boolean equals(Object val) {
        if (val instanceof Schedule) {
            return (getId() == ((Schedule) val).getId());
        }
        return super.equals(val);
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCurrentState() {
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

    public String getLastRunStatus() {
        return lastRunStatus;
    }

    public Date getLastRunTime() {
        return lastRunTime;
    }

    public Date getManualStartTime() {
        return manualStartTime;
    }

    public Date getManualStopTime() {
        return manualStopTime;
    }

    public Date getNextRunTime() {
        if (nextRunTime == null) {
            nextRunTime = new GregorianCalendar().getTime();
        }
        return nextRunTime;
    }

    public Date getNextStopTime() {
        if (nextStopTime == null) {
            nextStopTime = new GregorianCalendar().getTime();
        }
        return nextStopTime;
    }

    public NonPersistantScheduleData getNonPersistantData() {
        if (nonPersistantData == null) {
            nonPersistantData = new NonPersistantScheduleData();
        }

        return nonPersistantData;
    }

    public int getRepeatInterval() {
        return repeatInterval;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public String getScriptFileName() {
        return scriptFileName;
    }

    public String getStartCommand() {
        return startCommand;
    }

    public int getStartDay() {
        return startDay;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public String getStartPolicy() {
        return startPolicy;
    }

    public String getStartTime() {
        return startTime;
    }

    public int getStartYear() {
        return startYear;
    }

    public String getStopCommand() {
        return stopCommand;
    }

    public String getStopPolicy() {
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

    public String getType() {
        return type;
    }

    public String getValidWeekDays() {
        return validWeekDays;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setHolidayScheduleId(int holidayScheduleId) {
        this.holidayScheduleId = holidayScheduleId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLastRunStatus(String lastRunStatus) {
        this.lastRunStatus = lastRunStatus;
    }

    public void setLastRunTime(Date lastRunTime) {
        this.lastRunTime = lastRunTime;
    }

    public void setManualStartTime(Date manualStartTime) {
        this.manualStartTime = manualStartTime;
    }

    public void setManualStopTime(Date manualStopTime) {
        this.manualStopTime = manualStopTime;
    }

    public void setNextRunTime(Date nextRunTime) {
        this.nextRunTime = nextRunTime;
    }

    public void setNextStopTime(Date nextStopTime) {
        this.nextStopTime = nextStopTime;
    }

    public void setNonPersistantData(NonPersistantScheduleData nonPersistantData) {
        this.nonPersistantData = nonPersistantData;
    }

    public void setRepeatInterval(int repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public void setScriptFileName(String scriptFileName) {
        this.scriptFileName = scriptFileName;
        getNonPersistantData().getScript().setFileName(scriptFileName);
    }

    public void setStartCommand(String startCommand) {
        this.startCommand = startCommand;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public void setStartPolicy(String startPolicy) {
        this.startPolicy = startPolicy;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public void setStopCommand(String stopCommand) {
        this.stopCommand = stopCommand;
    }

    public void setStopPolicy(String stopPolicy) {
        this.stopPolicy = stopPolicy;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public void setTargetPAObjectId(int targetPAObjectId) {
        this.targetPAObjectId = targetPAObjectId;
    }

    public void setTemplateType(int templateType) {
        this.templateType = templateType;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValidWeekDays(String validWeekDays) {
        this.validWeekDays = validWeekDays;
    }

    public boolean getUpdatingState() {
        return updatingState;
    }

    public void setUpdatingState(boolean updatingState) {
        this.updatingState = updatingState;
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        try {
            PaoType paoType = PaoType.getForDbString(type);
            return new PaoIdentifier(id, paoType);
        } catch (IllegalArgumentException e) {
            // We don't have a valid PaoType yet.
            return null;
        }
    }
}
