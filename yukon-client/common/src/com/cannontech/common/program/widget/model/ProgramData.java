package com.cannontech.common.program.widget.model;

import java.util.List;

import org.joda.time.DateTime;

public class ProgramData {

    private int programId;
    private int programHistoryId;
    private String programName;
    private DateTime startDateTime;
    private DateTime stopDateTime;
    private DateTime eventTime;
    private List<GearData> gears;

    private ProgramData(ProgramDataBuilder programDataBuilder) {
        this.programId = programDataBuilder.programId;
        this.programHistoryId = programDataBuilder.programHistoryId;
        this.programName = programDataBuilder.programName;
        this.eventTime = programDataBuilder.eventTime;
        this.startDateTime = programDataBuilder.startDateTime;
        this.stopDateTime = programDataBuilder.stopDateTime;
        this.gears = programDataBuilder.gears;
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

    public DateTime getStartDateTime() {
        return startDateTime;
    }

    public DateTime getStopDateTime() {
        return stopDateTime;
    }

    public DateTime getEventTime() {
        return eventTime;
    }

    public List<GearData> getGears() {
        return gears;
    }

    public static class ProgramDataBuilder {
        private int programId;
        private int programHistoryId;
        private String programName;
        private DateTime startDateTime;
        private DateTime stopDateTime;
        private DateTime eventTime;
        private List<GearData> gears;

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

        public ProgramDataBuilder setStartDateTime(DateTime startDateTime) {
            this.startDateTime = startDateTime;
            return this;
        }

        public ProgramDataBuilder setStopDateTime(DateTime stopDateTime) {
            this.stopDateTime = stopDateTime;
            return this;
        }

        public ProgramDataBuilder setEventTime(DateTime eventTime) {
            this.eventTime = eventTime;
            return this;
        }

        public ProgramDataBuilder setGears(List<GearData> gears) {
            this.gears = gears;
            return this;
        }

    }

}
