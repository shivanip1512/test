package com.cannontech.common.dr.setup;

import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(value = { "controlAreaId" }, allowGetters = true, ignoreUnknown = true)
public class ControlArea {

    private Integer controlAreaId;
    private String name;
    private Integer controlInterval;
    private Integer minResponseTime;
    private DailyDefaultState dailyDefaultState;
    private Integer dailyStartTimeInMinutes;
    private Integer dailyStopTimeInMinutes;
    private Boolean allTriggersActiveFlag;

    private List<ControlAreaTrigger> triggers;
    private List<ControlAreaProgramAssignment> programAssignment;

    @JsonIgnore
    private Comparator<ControlAreaProgramAssignment> startPriorityComparator = (o1, o2) -> {
        return o1.getStartPriority().compareTo(o2.getStartPriority());
    };

    @JsonIgnore
    private Comparator<ControlAreaProgramAssignment> stopPriorityComparator = (o1, o2) -> {
        return o1.getStopPriority().compareTo(o2.getStopPriority());
    };

    @JsonIgnore
    private Comparator<ControlAreaProgramAssignment> nameComparator = (o1, o2) -> {
        return o1.getProgramName().compareToIgnoreCase(o2.getProgramName());
    };

    public Integer getControlAreaId() {
        return controlAreaId;
    }

    public void setControlAreaId(Integer controlAreaId) {
        this.controlAreaId = controlAreaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getControlInterval() {
        return controlInterval;
    }

    public void setControlInterval(Integer controlInterval) {
        this.controlInterval = controlInterval;
    }

    public Integer getMinResponseTime() {
        return minResponseTime;
    }

    public void setMinResponseTime(Integer minResponseTime) {
        this.minResponseTime = minResponseTime;
    }

    public DailyDefaultState getDailyDefaultState() {
        return dailyDefaultState;
    }

    public void setDailyDefaultState(DailyDefaultState dailyDefaultState) {
        this.dailyDefaultState = dailyDefaultState;
    }

    public Integer getDailyStartTimeInMinutes() {
        return dailyStartTimeInMinutes;
    }

    public void setDailyStartTimeInMinutes(Integer dailyStartTimeInMinutes) {
        this.dailyStartTimeInMinutes = dailyStartTimeInMinutes;
    }

    public Integer getDailyStopTimeInMinutes() {
        return dailyStopTimeInMinutes;
    }

    public void setDailyStopTimeInMinutes(Integer dailyStopTimeInMinutes) {
        this.dailyStopTimeInMinutes = dailyStopTimeInMinutes;
    }

    public Boolean getAllTriggersActiveFlag() {
        return allTriggersActiveFlag;
    }

    public void setAllTriggersActiveFlag(Boolean allTriggersActiveFlag) {
        this.allTriggersActiveFlag = allTriggersActiveFlag;
    }

    public List<ControlAreaTrigger> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<ControlAreaTrigger> triggers) {
        this.triggers = triggers;
    }

    public List<ControlAreaProgramAssignment> getProgramAssignment() {
        return programAssignment;
    }

    public void setProgramAssignment(List<ControlAreaProgramAssignment> programAssignment) {
        this.programAssignment = programAssignment;
    }

    public Comparator<ControlAreaProgramAssignment> getStartPriorityComparator() {
        return startPriorityComparator;
    }

    public Comparator<ControlAreaProgramAssignment> getStopPriorityComparator() {
        return stopPriorityComparator;
    }

    public Comparator<ControlAreaProgramAssignment> getNameComparator() {
        return nameComparator;
    }

}
