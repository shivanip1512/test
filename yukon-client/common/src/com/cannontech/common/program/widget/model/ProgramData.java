package com.cannontech.common.program.widget.model;

import java.util.List;

import org.joda.time.DateTime;

import com.cannontech.dr.model.ProgramOriginSource;

public class ProgramData {

    private int programId;
    private String programName;
    private DateTime startDateTime;
    private ProgramOriginSource originSource;
    private int programHistoryId;
    private String statusCssClass;

    private List<GearData> gears;

    private ProgramData(ProgramDataBuilder programDataBuilder) {
        this.programId = programDataBuilder.programId;
        this.programName = programDataBuilder.programName;
        this.startDateTime = programDataBuilder.startDateTime;
        this.originSource = programDataBuilder.originSource;
        this.gears = programDataBuilder.gears;
        this.programHistoryId = programDataBuilder.programHistoryId;
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

    public List<GearData> getGears() {
        return gears;
    }

    public ProgramOriginSource getOriginSource() {
        return originSource;
    }

    public int getProgramHistoryId() {
        return programHistoryId;
    }

    public String getStatusCssClass() {
        return statusCssClass;
    }

    /**
     * It is not recommended to add setter in Builder design pattern.
     * Adding it exceptionally.
     */
    public void setGears(List<GearData> gears) {
        this.gears = gears;
    }

    public void setOriginSource(ProgramOriginSource originSource) {
        this.originSource = originSource;
    }

    public void setStatusCssClass(String statusCssClass) {
        this.statusCssClass = statusCssClass;
    }

    public static class ProgramDataBuilder {
        private int programId;
        private String programName;
        private DateTime startDateTime;
        private ProgramOriginSource originSource;
        private int programHistoryId;

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

        public ProgramDataBuilder setOriginSource(ProgramOriginSource originSource) {
            this.originSource = originSource;
            return this;
        }

        public ProgramDataBuilder setProgramHistoryId(int programHistoryId) {
            this.programHistoryId = programHistoryId;
            return this;
        }
    }

}
