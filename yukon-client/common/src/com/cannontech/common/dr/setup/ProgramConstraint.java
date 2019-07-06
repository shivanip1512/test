package com.cannontech.common.dr.setup;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cannontech.database.db.device.lm.LMProgramConstraint;

public class ProgramConstraint {
    private Integer id;
    private String name;
    private LMDto seasonSchedule;
    private Integer maxActivateTime;
    private Integer maxDailyOps;
    private Integer minActivateTime;
    private Integer minRestartTime;
    private List<DayOfWeek> daySelection;
    private LMDto holidaySchedule;
    private HolidayUsage holidayUsage;
    private Integer maxHoursDaily;
    private Integer maxHoursMonthly;
    private Integer maxHoursAnnually;
    private Integer maxHoursSeasonal;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LMDto getSeasonSchedule() {
        return seasonSchedule;
    }

    public void setSeasonSchedule(LMDto seasonSchedule) {
        this.seasonSchedule = seasonSchedule;
    }

    public Integer getMaxActivateTime() {
        return maxActivateTime;
    }

    public void setMaxActivateTime(Integer maxActivateTime) {
        this.maxActivateTime = maxActivateTime;
    }

    public Integer getMaxDailyOps() {
        return maxDailyOps;
    }

    public void setMaxDailyOps(Integer maxDailyOps) {
        this.maxDailyOps = maxDailyOps;
    }

    public Integer getMinActivateTime() {
        return minActivateTime;
    }

    public void setMinActivateTime(Integer minActivateTime) {
        this.minActivateTime = minActivateTime;
    }

    public Integer getMinRestartTime() {
        return minRestartTime;
    }

    public void setMinRestartTime(Integer minRestartTime) {
        this.minRestartTime = minRestartTime;
    }

    public List<DayOfWeek> getDaySelection() {
        return daySelection;
    }

    public void setDaySelection(List<DayOfWeek> daySelection) {
        this.daySelection = daySelection;
    }

    public LMDto getHolidaySchedule() {
        return holidaySchedule;
    }

    public void setHolidaySchedule(LMDto holidaySchedule) {
        this.holidaySchedule = holidaySchedule;
    }

    public HolidayUsage getHolidayUsage() {
        return holidayUsage;
    }

    public void setHolidayUsage(HolidayUsage holidayUsage) {
        this.holidayUsage = holidayUsage;
    }

    public Integer getMaxHoursDaily() {
        return maxHoursDaily;
    }

    public void setMaxHoursDaily(Integer maxHoursDaily) {
        this.maxHoursDaily = maxHoursDaily;
    }

    public Integer getMaxHoursMonthly() {
        return maxHoursMonthly;
    }

    public void setMaxHoursMonthly(Integer maxHoursMonthly) {
        this.maxHoursMonthly = maxHoursMonthly;
    }

    public Integer getMaxHoursAnnually() {
        return maxHoursAnnually;
    }

    public void setMaxHoursAnnually(Integer maxHoursAnnually) {
        this.maxHoursAnnually = maxHoursAnnually;
    }

    public Integer getMaxHoursSeasonal() {
        return maxHoursSeasonal;
    }

    public void setMaxHoursSeasonal(Integer maxHoursSeasonal) {
        this.maxHoursSeasonal = maxHoursSeasonal;
    }

    public void buildModel(LMProgramConstraint lMProgramConstraint) {
        setId(lMProgramConstraint.getConstraintID());
        setName(lMProgramConstraint.getConstraintName());
        setDaySelection(DayOfWeek.buildModelRepresentation(lMProgramConstraint.getAvailableWeekdays().substring(0, 7)));
        setMaxHoursDaily(lMProgramConstraint.getMaxHoursDaily());
        setMaxHoursMonthly(lMProgramConstraint.getMaxHoursMonthly());
        setMaxHoursSeasonal(lMProgramConstraint.getMaxHoursSeasonal());
        setMaxHoursAnnually(lMProgramConstraint.getMaxHoursAnnually());
        setMinActivateTime(lMProgramConstraint.getMinActivateTime());
        setMinRestartTime(lMProgramConstraint.getMinRestartTime());
        setMaxDailyOps(lMProgramConstraint.getMaxDailyOps());
        setMaxActivateTime(lMProgramConstraint.getMaxActivateTime());
        Integer holidayScheduleId = lMProgramConstraint.getHolidayScheduleID();
        LMDto holidaySchedule = new LMDto();
        holidaySchedule.setId(holidayScheduleId);
        setHolidaySchedule(holidaySchedule);
        Integer seasonScheduleId = lMProgramConstraint.getSeasonScheduleID();
        LMDto seasonSchedule = new LMDto();
        seasonSchedule.setId(seasonScheduleId);
        setSeasonSchedule(seasonSchedule);
        String holidayUsage = lMProgramConstraint.getAvailableWeekdays().substring(7, 8);
        if (holidayUsage.equals("E")) {
            setHolidayUsage(HolidayUsage.Exclude);
        } else {
            setHolidayUsage(HolidayUsage.Force);
        }
    }

    public void buildDBPersistent(LMProgramConstraint lMProgramConstraint) {
        lMProgramConstraint.setConstraintID(getId());
        lMProgramConstraint.setConstraintName(getName());
        String holidayUsage = StringUtils.EMPTY;
        if (getHolidaySchedule() != null) {
            //When No schedule is selected, ID will be 0
            if (getHolidaySchedule().getId().compareTo(0) == 0) {
                holidayUsage = "N";
            } else {
                holidayUsage = (String) getHolidayUsage().getHolidayUsage();
            }
        }
        lMProgramConstraint.setAvailableWeekdays(DayOfWeek.buildDBPersistent(getDaySelection()).concat(holidayUsage));
        lMProgramConstraint.setMaxHoursDaily(getMaxHoursDaily());
        lMProgramConstraint.setMaxHoursMonthly(getMaxHoursMonthly());
        lMProgramConstraint.setMaxHoursSeasonal(getMaxHoursSeasonal());
        lMProgramConstraint.setMaxHoursAnnually(getMaxHoursAnnually());
        lMProgramConstraint.setMinActivateTime(getMinActivateTime());
        lMProgramConstraint.setMinRestartTime(getMinRestartTime());
        lMProgramConstraint.setMaxDailyOps(getMaxDailyOps());
        lMProgramConstraint.setMaxActivateTime(getMaxActivateTime());
        lMProgramConstraint.setHolidayScheduleID(getHolidaySchedule().getId());
        lMProgramConstraint.setSeasonScheduleID(getSeasonSchedule().getId());
    }
}
