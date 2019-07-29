package com.cannontech.loadcontrol.dao;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Date;

import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;


public class LmProgramGearHistory {

    private int programId;
    private String programName;

    private int programGearHistoryId;
    private int programHistoryId;
    private Date eventTime;
    private String action;
    private String userName;
    private String gearName;
    private int gearId;
    private String reason;
    private String originSource;

public enum GearAction implements DatabaseRepresentationSource {

        START("Start"),
        GEAR_CHANGE("Gear Change"),
        STOP("Stop"),
        UNKNOWN("Unknown");

        String dbString;

        private GearAction(String dbString) {
            this.dbString = dbString;
        }

        private final static ImmutableMap<String, GearAction> lookupByDbString;

        static {
            ImmutableMap.Builder<String, GearAction> dbBuilder = ImmutableMap.builder();
            for (GearAction gearAction : values()) {
                dbBuilder.put(gearAction.dbString, gearAction);
            }
            lookupByDbString = dbBuilder.build();
        }

        public static GearAction getForDbString(String dbString) throws IllegalArgumentException {
            GearAction gearAction = lookupByDbString.get(dbString);
            checkArgument(gearAction != null, dbString);
            return gearAction;
        }

        @Override
        public Object getDatabaseRepresentation() {
            return dbString;
        }
    }

    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public int getProgramGearHistoryId() {
        return programGearHistoryId;
    }

    public void setProgramGearHistoryId(int programGearHistoryId) {
        this.programGearHistoryId = programGearHistoryId;
    }

    public int getProgramHistoryId() {
        return programHistoryId;
    }

    public void setProgramHistoryId(int programHistoryId) {
        this.programHistoryId = programHistoryId;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGearName() {
        return gearName;
    }

    public void setGearName(String gearName) {
        this.gearName = gearName;
    }

    public int getGearId() {
        return gearId;
    }

    public void setGearId(int gearId) {
        this.gearId = gearId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOriginSource() {
        return originSource;
    }

    public void setOriginSource(String originSource) {
        this.originSource = originSource;
    }
}
