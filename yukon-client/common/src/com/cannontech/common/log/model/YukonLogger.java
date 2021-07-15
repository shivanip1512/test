package com.cannontech.common.log.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class YukonLogger {
    private int loggerId;
    private LoggerType loggerType;
    private LoggerLevel level;
    private String loggerName;
    private Date expirationDate;
    private String notes;

    public YukonLogger() {
    }

    public YukonLogger(int loggerId, LoggerLevel level, String loggerName, Date expirationDate, String notes) {
        this.setLoggerId(loggerId);
        this.level = level;
        this.loggerName = loggerName;
        this.expirationDate = expirationDate;
        this.notes = notes;
        this.loggerType = SystemLogger.isSystemLogger(loggerName) ? LoggerType.SYSTEM_LOGGER : LoggerType.USER_LOGGER;
    }

    public int getLoggerId() {
        return loggerId;
    }

    public void setLoggerId(int loggerId) {
        this.loggerId = loggerId;
    }

    public LoggerType getLoggerType() {
        return loggerType;
    }

    public void setLoggerType(LoggerType loggerType) {
        this.loggerType = loggerType;
    }

    public LoggerLevel getLevel() {
        return level;
    }

    public void setLevel(LoggerLevel level) {
        this.level = level;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
        setLoggerType(SystemLogger.isSystemLogger(loggerName) ? LoggerType.SYSTEM_LOGGER : LoggerType.USER_LOGGER);
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}