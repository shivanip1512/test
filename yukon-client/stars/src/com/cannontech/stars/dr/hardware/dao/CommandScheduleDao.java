package com.cannontech.stars.dr.hardware.dao;

import java.util.List;

import com.cannontech.stars.dr.hardware.model.CommandSchedule;

public interface CommandScheduleDao {
    public CommandSchedule getById(int commandScheduleId);

    public List<CommandSchedule> getAll(int energyCompanyId);

    public List<CommandSchedule> getAllEnabled(int energyCompanyId);

    public void save(CommandSchedule schedule);

    public int delete(int scheduleId);

    public void disable(int scheduleId);
    
    public void enable(int scheduleId);

    public void disableAll();
}
