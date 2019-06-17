package com.cannontech.common.program.widget.model;

import java.util.List;

import org.joda.time.DateTime;

public class ProgramData {

    private int programId;
    private String programName;
    private DateTime startDateTime;
    private String status;
    private List<GearData> gears;

    private ProgramData(ProgramDataBuilder programDataBuilder) {
        this.programId = programDataBuilder.programId;
        this.programName = programDataBuilder.programName;
        this.startDateTime = programDataBuilder.startDateTime;
        this.status = programDataBuilder.status;
        this.gears = programDataBuilder.gears;
    }

    public int getProgramId() {
        return programId;
    }

    public String getProgramName() {
        return programName;
    }

    public DateTime getStartDateTime() {
        return startDateTime;
    }

    public String getStatus() {
        return status;
    }

    public List<GearData> getGears() {
        return gears;
    }

    public static class ProgramDataBuilder {
        private int programId;
        private String programName;
        private DateTime startDateTime;
        private String status;
        private List<GearData> gears;

        public ProgramDataBuilder(int programId) {
            this.programId = programId;
        }

        public ProgramData build() {
            return new ProgramData(this);
        }

        public ProgramDataBuilder setProgramName(String programName) {
            this.programName = programName;
            return this;
        }

        public ProgramDataBuilder setStartDateTime(DateTime startDateTime) {
            this.startDateTime = startDateTime;
            return this;
        }

        public ProgramDataBuilder setGears(List<GearData> gears) {
            this.gears = gears;
            return this;
        }

        public ProgramDataBuilder setStatus(String status) {
            this.status = status;
            return this;
        }

    }

}
