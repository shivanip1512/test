package com.cannontech.common.dr.setup;

import java.util.List;
import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.db.device.lm.LMProgramConstraint;

public class ProgramConstraint implements DBPersistentConverter<LMProgramConstraint> {
    private Integer id;
    private String name;
    private LMDto seasonSchedule;
    private Integer maxActivateSeconds;
    private Integer maxDailyOps;
    private Integer minActivateSeconds;
    private Integer minRestartSeconds;
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

    public Integer getMaxActivateSeconds() {
        return maxActivateSeconds;
    }

    public void setMaxActivateSeconds(Integer maxActivateSeconds) {
        this.maxActivateSeconds = maxActivateSeconds;
    }

    public Integer getMaxDailyOps() {
        return maxDailyOps;
    }

    public void setMaxDailyOps(Integer maxDailyOps) {
        this.maxDailyOps = maxDailyOps;
    }

    public Integer getMinActivateSeconds() {
        return minActivateSeconds;
    }

    public void setMinActivateSeconds(Integer minActivateSeconds) {
        this.minActivateSeconds = minActivateSeconds;
    }

    public Integer getMinRestartSeconds() {
        return minRestartSeconds;
    }

    public void setMinRestartSeconds(Integer minRestartSeconds) {
        this.minRestartSeconds = minRestartSeconds;
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
        if (getHolidaySchedule() != null && getHolidaySchedule().getId() != null && getHolidaySchedule().getId() == 0) {
            return HolidayUsage.NONE;
        } else {
            return holidayUsage;
        }
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

    @Override
    public void buildModel(LMProgramConstraint lMProgramConstraint) {
        setId(lMProgramConstraint.getConstraintID());
        setName(lMProgramConstraint.getConstraintName());
        setDaySelection(DayOfWeek.buildModelRepresentation(lMProgramConstraint.getAvailableWeekdays().substring(0, 7)));
        setMaxHoursDaily(lMProgramConstraint.getMaxHoursDaily());
        setMaxHoursMonthly(lMProgramConstraint.getMaxHoursMonthly());
        setMaxHoursSeasonal(lMProgramConstraint.getMaxHoursSeasonal());
        setMaxHoursAnnually(lMProgramConstraint.getMaxHoursAnnually());
        setMinActivateSeconds(lMProgramConstraint.getMinActivateTime());
        setMinRestartSeconds(lMProgramConstraint.getMinRestartTime());
        setMaxDailyOps(lMProgramConstraint.getMaxDailyOps());
        setMaxActivateSeconds(lMProgramConstraint.getMaxActivateTime());
        Integer holidayScheduleId = lMProgramConstraint.getHolidayScheduleID();
        LMDto holidaySchedule = new LMDto();
        holidaySchedule.setId(holidayScheduleId);
        setHolidaySchedule(holidaySchedule);
        Integer seasonScheduleId = lMProgramConstraint.getSeasonScheduleID();
        LMDto seasonSchedule = new LMDto();
        seasonSchedule.setId(seasonScheduleId);
        setSeasonSchedule(seasonSchedule);
        Character holidayUsage = lMProgramConstraint.getAvailableWeekdays().charAt(7);
        setHolidayUsage(HolidayUsage.getForHoliday(holidayUsage));
    }

    @Override
    public void buildDBPersistent(LMProgramConstraint lMProgramConstraint) {
        lMProgramConstraint.setConstraintID(getId());
        lMProgramConstraint.setConstraintName(getName());
        Character holidayUsage = getHolidayUsage().getHolidayUsage();
        lMProgramConstraint.setAvailableWeekdays(DayOfWeek.buildDBPersistent(getDaySelection()) + holidayUsage);
        lMProgramConstraint.setMaxHoursDaily(getMaxHoursDaily());
        lMProgramConstraint.setMaxHoursMonthly(getMaxHoursMonthly());
        lMProgramConstraint.setMaxHoursSeasonal(getMaxHoursSeasonal());
        lMProgramConstraint.setMaxHoursAnnually(getMaxHoursAnnually());
        lMProgramConstraint.setMinActivateTime(getMinActivateSeconds());
        lMProgramConstraint.setMinRestartTime(getMinRestartSeconds());
        lMProgramConstraint.setMaxDailyOps(getMaxDailyOps());
        lMProgramConstraint.setMaxActivateTime(getMaxActivateSeconds());
        lMProgramConstraint.setHolidayScheduleID(getHolidaySchedule().getId());
        lMProgramConstraint.setSeasonScheduleID(getSeasonSchedule().getId());
    }
}
