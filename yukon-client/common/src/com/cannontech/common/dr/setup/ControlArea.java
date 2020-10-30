package com.cannontech.common.dr.setup;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.TFBoolean;
import com.cannontech.database.data.device.lm.LMControlArea;
import com.cannontech.database.db.device.lm.LMControlAreaProgram;
import com.cannontech.database.db.device.lm.LMControlAreaTrigger;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(value = { "controlAreaId" }, allowGetters = true, ignoreUnknown = true)
public class ControlArea implements DBPersistentConverter<LMControlArea> {

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

    /**
     * Build Control Area Model object.
     */
    @Override
    public void buildModel(LMControlArea lmControlArea) {
        com.cannontech.database.db.device.lm.LMControlArea lmcontrolarea = lmControlArea.getControlArea();
        setControlAreaId(lmControlArea.getPAObjectID());
        setName(lmControlArea.getPAOName());

        setControlInterval(lmcontrolarea.getControlInterval());

        setMinResponseTime(lmcontrolarea.getMinResponseTime());

        setDailyDefaultState(DailyDefaultState.valueOf(lmcontrolarea.getDefOperationalState()));

        setDailyStartTimeInMinutes(
                lmcontrolarea
                        .getDefDailyStartTime() == com.cannontech.database.db.device.lm.LMControlArea.OPTIONAL_VALUE_UNUSED ? null : lmcontrolarea
                                .getDefDailyStartTime() / 60);

        setDailyStopTimeInMinutes(
                lmcontrolarea
                        .getDefDailyStopTime() == com.cannontech.database.db.device.lm.LMControlArea.OPTIONAL_VALUE_UNUSED ? null : lmcontrolarea
                                .getDefDailyStopTime() / 60);

        if (lmcontrolarea.getRequireAllTriggersActiveFlag().equals(TFBoolean.TRUE.getDatabaseRepresentation())) {
            setAllTriggersActiveFlag(true);
        } else {
            setAllTriggersActiveFlag(false);
        }

        // Build model for Trigger
        Integer triggerOrder = 1;
        List<ControlAreaTrigger> areaTriggers = new ArrayList<>();
        for (LMControlAreaTrigger areaTrigger : lmControlArea.getLmControlAreaTriggerVector()) {
            ControlAreaTrigger trigger = new ControlAreaTrigger();
            trigger.buildModel(areaTrigger);
            trigger.setTriggerNumber(triggerOrder);
            areaTriggers.add(trigger);
            triggerOrder++;
        }
        if (CollectionUtils.isNotEmpty(areaTriggers)) {
            setTriggers(areaTriggers);
        }

        // Build model for ProgramAssignment
        List<ControlAreaProgramAssignment> programAssignment = new ArrayList<>();
        lmControlArea.getLmControlAreaProgramVector().forEach(program -> {
            ControlAreaProgramAssignment controlAreaProgramAssignment = new ControlAreaProgramAssignment();
            controlAreaProgramAssignment.buildModel(program);
            programAssignment.add(controlAreaProgramAssignment);
        });
        if (CollectionUtils.isNotEmpty(programAssignment)) {
            setProgramAssignment(programAssignment);
        }
    }


    /**
     * Build DB Persistent object for LMControlArea.
     */
    @Override
    public void buildDBPersistent(LMControlArea lmControlArea) {
        lmControlArea.setPAOName(getName());
        com.cannontech.database.db.device.lm.LMControlArea lmDbControlArea = lmControlArea.getControlArea();
        lmDbControlArea.setControlInterval(getControlInterval());
        lmDbControlArea.setMinResponseTime(getMinResponseTime());

        lmDbControlArea.setDefOperationalState(getDailyDefaultState().name());

        if (getAllTriggersActiveFlag() != null) {
            TFBoolean triggersActive = TFBoolean.valueOf(getAllTriggersActiveFlag());
            lmDbControlArea.setRequireAllTriggersActiveFlag((Character) triggersActive.getDatabaseRepresentation());
        }

        if (getDailyStartTimeInMinutes() != null) {
            lmDbControlArea.setDefDailyStartTime(getDailyStartTimeInMinutes() * 60);
        } else {
            lmDbControlArea.setDefDailyStartTime(com.cannontech.database.db.device.lm.LMControlArea.OPTIONAL_VALUE_UNUSED);
        }

        if (getDailyStopTimeInMinutes() != null) {
            lmDbControlArea.setDefDailyStopTime(getDailyStopTimeInMinutes() * 60);
        } else {
            lmDbControlArea.setDefDailyStopTime(com.cannontech.database.db.device.lm.LMControlArea.OPTIONAL_VALUE_UNUSED);
        }

        // Build DBPersistance for Trigger
        Integer triggerOrder = 1;
        if (CollectionUtils.isNotEmpty(lmControlArea.getLmControlAreaTriggerVector())) {
            lmControlArea.getLmControlAreaTriggerVector().clear();
        }
        if (CollectionUtils.isNotEmpty(getTriggers())) {
            for (ControlAreaTrigger areaTrigger : getTriggers()) {
                LMControlAreaTrigger lmControlAreaTrigger = new LMControlAreaTrigger();
                areaTrigger.buildDBPersistent(lmControlAreaTrigger);
                lmControlAreaTrigger.setTriggerNumber(triggerOrder);
                lmControlArea.getLmControlAreaTriggerVector().add(lmControlAreaTrigger);
                triggerOrder++;
            }
        }
        // Build DBPersistance for ProgramAssignment 
        if (CollectionUtils.isNotEmpty(lmControlArea.getLmControlAreaProgramVector())) {
            lmControlArea.getLmControlAreaProgramVector().clear();
        }
        if (CollectionUtils.isNotEmpty(getProgramAssignment())) {
            for (ControlAreaProgramAssignment assignedProgram : getProgramAssignment()) {
                LMControlAreaProgram lmControlAreaProgram = new LMControlAreaProgram();
                assignedProgram.buildDBPersistent(lmControlAreaProgram);
                lmControlArea.getLmControlAreaProgramVector().add(lmControlAreaProgram);
            }
        }
    }

}
