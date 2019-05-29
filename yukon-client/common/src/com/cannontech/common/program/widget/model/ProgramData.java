package com.cannontech.common.program.widget.model;

import java.util.Date;

public class ProgramData {

    private int programId;
    private int programHistoryId;
    private String programName;
    private Date startDateTime;
    private Date stopDateTime;
    private String gearName;
    private String action;
    private Date eventTime;

    private ProgramData(ProgramDataBuilder programDataBuilder) {
        this.programId = programDataBuilder.programId;
        this.programHistoryId = programDataBuilder.programHistoryId;
        this.startDateTime = programDataBuilder.startDateTime;
        this.stopDateTime = programDataBuilder.stopDateTime;
        this.programName = programDataBuilder.programName;
        this.gearName = programDataBuilder.gearName;
        this.action = programDataBuilder.action;
        this.eventTime = programDataBuilder.eventTime;
    }

    public int getProgramId() {
        return programId;
    }

    public int getProgramHistoryId() {
        return programHistoryId;
    }

    public String getProgramName() {
        return programName;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public Date getStopDateTime() {
        return stopDateTime;
    }

    public String getGearName() {
        return gearName;
    }

    public String getAction() {
        return action;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public static class ProgramDataBuilder {
        private int programId;
        private int programHistoryId;
        private String programName;
        private String gearName;
        private String action;
        private Date startDateTime;
        private Date stopDateTime;
        private Date eventTime;

        public ProgramDataBuilder(int programId) {
            this.programId = programId;
        }

        public ProgramData build() {
            return new ProgramData(this);
        }

        public ProgramDataBuilder setProgramHistoryId(int programHistoryId) {
            this.programHistoryId = programHistoryId;
            return this;
        }

        public ProgramDataBuilder setProgramName(String programName) {
            this.programName = programName;
            return this;
        }

        public ProgramDataBuilder setGearName(String gearName) {
            this.gearName = gearName;
            return this;
        }

        public ProgramDataBuilder setAction(String action) {
            this.action = action;
            return this;
        }

        public ProgramDataBuilder setStartDateTime(Date startDateTime) {
            this.startDateTime = startDateTime;
            return this;
        }

        public ProgramDataBuilder setStopDateTime(Date stopDateTime) {
            this.stopDateTime = stopDateTime;
            return this;
        }

        public ProgramDataBuilder setEventTime(Date eventTime) {
            this.eventTime = eventTime;
            return this;
        }
    }

}
